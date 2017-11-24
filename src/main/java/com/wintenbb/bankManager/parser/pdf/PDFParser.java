package com.wintenbb.bankManager.parser.pdf;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

import com.wintenbb.bankManager.domain.Account;
import com.wintenbb.bankManager.domain.AccountLine;
import com.wintenbb.bankManager.domain.AccountsStatement;
import com.wintenbb.bankManager.parser.pdf.config.PDFPageConfig;
import com.wintenbb.bankManager.parser.pdf.config.PDFParserConfig;

public class PDFParser {
	private static final Pattern SUITE_AU_VERSO = Pattern.compile("(\\s*<<Suite au verso>>)");
	private static final Pattern ACCOUNT_PATTERN = Pattern.compile("([\\w\\s]+) N° (\\d+)");
	private static final Pattern LINE_PATTERN = Pattern.compile("^(\\d{2}/\\d{2}/\\d{4}) .*");
	private static final Pattern SOLDE_NUL_PATTERN = Pattern.compile("SOLDE NUL");
	private static final Pattern SOLDE_CREDITEUR_PATTERN = Pattern.compile("SOLDE CREDITEUR .*/\\d{4} *([-\\.,\\d]+)");
	private static final Pattern LINE_COMPLEMENTARY_PATTERN = Pattern.compile(".+");
	private static final Pattern SITUATION_OTHER_ACCOUNT = Pattern.compile("(\\d{8,12}) *(.*) * EUR *(.*)");

	private PDFParserConfig parserConf;
	private PDDocument doc;
	private List<PDPage> allPages;
	private PDPage currPage;
	private int lastHeight = 0;
	private int lastPage = 0;
	private int page = 0;
	private int lastOffset = 0;
	private int yOffset = 50;
	private String readText = null;
	private String lastLine = null;

	private boolean finished = false;

	/**
	 * Constructor
	 *
	 * @param is
	 *            inputStream
	 * @param parserConf
	 *            configuration
	 * @throws IOException
	 *             when an error occurred reading account file
	 */
	@SuppressWarnings("unchecked")
	public PDFParser(InputStream is, PDFParserConfig parserConf) throws IOException {
		org.apache.pdfbox.pdfparser.PDFParser parser = new org.apache.pdfbox.pdfparser.PDFParser(is);
		parser.parse();
		doc = parser.getPDDocument();

		allPages = doc.getDocumentCatalog().getAllPages();
		currPage = allPages.get(page);

		this.parserConf = parserConf;
	}

	public AccountsStatement treat() throws IOException {
		try {
			AccountsStatement extrait = new AccountsStatement();
			Account currAccount = null;
			String currentAccountName;
			boolean lastReadWasLine = false;
			boolean suiteAuVerso = false;
			while (!finished) {
				readNextLine();
				while (lastLine == null && !finished) {
					readNextLine();
				}
				if (finished) {
					break;
				}

				Matcher m;
				if ((m = ACCOUNT_PATTERN.matcher(lastLine)).find()) {
					currentAccountName = m.group(2).trim();
					if (!extrait.getAccounts().containsKey(currentAccountName)) {
						currAccount = new Account();
						currAccount.setAccountName(m.group(1));
						currAccount.setAccountNumber(currentAccountName);
						extrait.getAccounts().put(currentAccountName, currAccount);
					} else {
						currAccount = extrait.getAccounts().get(currentAccountName);
					}
					if (suiteAuVerso) {
						lastReadWasLine = true;
						suiteAuVerso = false;
						readNextLine();// read header line
						readNextLine();// read header line
						readNextLine();// read header line
					} else {
						lastReadWasLine = false;
					}
				} else if (SUITE_AU_VERSO.matcher(lastLine).find()) {
					nextPage();
					suiteAuVerso = true;
				} else if ((m = SOLDE_CREDITEUR_PATTERN.matcher(lastLine)).find()) {
					if (currAccount.getInitialBalance() == null) {
						currAccount
								.setInitialBalance(new BigDecimal(m.group(1).replaceAll("\\.", "").replace(",", ".")));
					} else {
						currAccount.setFinalBalance(new BigDecimal(m.group(1).replaceAll("\\.", "").replace(",", ".")));
					}
					lastReadWasLine = false;
				} else if (SOLDE_NUL_PATTERN.matcher(lastLine).find()) {
					if (currAccount.getInitialBalance() == null) {
						currAccount.setInitialBalance(BigDecimal.ZERO);
					} else {
						currAccount.setFinalBalance(BigDecimal.ZERO);
					}
					lastReadWasLine = false;
				} else if ((m = SITUATION_OTHER_ACCOUNT.matcher(lastLine)).find()) {
					currentAccountName = m.group(1).trim();
					if (!extrait.getAccounts().containsKey(currentAccountName)) {
						currAccount = new Account();
						currAccount.setAccountNumber(currentAccountName);
						currAccount.setAccountName(m.group(2));
						extrait.getAccounts().put(currentAccountName, currAccount);
					}
					System.out.println(m.group(3));
					BigDecimal solde = new BigDecimal(m.group(3).replaceAll("\\.", "").replace(",", "."));
					currAccount.setInitialBalance(solde);
					currAccount.setFinalBalance(solde);
					lastReadWasLine = false;
				} else if (LINE_PATTERN.matcher(lastLine).find()) {
					currAccount.addLine(parseLine());
					lastReadWasLine = true;
				} else if ((m = LINE_COMPLEMENTARY_PATTERN.matcher(lastLine)).find() && lastReadWasLine) {
					currAccount.getLastLine().setLabel2(m.group().trim());
					lastReadWasLine = false;
				}
			}

			if (check(extrait)) {
				return extrait;
			}
		} finally {
			close();
		}
		return null;
	}

	private AccountLine parseLine() throws IOException {
		PDFPageConfig pc = parserConf.configs.get(lastPage % 2);
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		int xOffset = pc.xOffset;
		for (int i = 0; i < pc.widths.length; i++) {
			Rectangle rect1 = new Rectangle(xOffset, lastOffset, pc.widths[i], lastHeight);
			stripper.addRegion("page" + page + "col" + i, rect1);
			xOffset += pc.widths[i];
		}
		stripper.extractRegions(currPage);
		AccountLine l = new AccountLine();
		l.setBeginDate(stripper.getTextForRegion("page" + page + "col0"));
		l.setValueDate(stripper.getTextForRegion("page" + page + "col1"));
		l.setLabel(stripper.getTextForRegion("page" + page + "col2").trim());
		l.setDebit(stripper.getTextForRegion("page" + page + "col3"));
		l.setCredit(stripper.getTextForRegion("page" + page + "col4"));
		return l;
	}

	private void readNextLine() throws IOException {
		int height = 1;
		while ((readText == null || readText.matches("\r\n")) && height + yOffset < 1200) {
			PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			stripper.setSortByPosition(true);
			Rectangle rect1 = new Rectangle(40, yOffset, 520, height);
			stripper.addRegion("region", rect1);
			yOffset += 1;
			stripper.extractRegions(currPage);
			readText = stripper.getTextForRegion("region");
			lastHeight = height;
			height++;
		}
		lastPage = page;
		lastLine = readText;
		readText = null;
		lastOffset = yOffset;
		yOffset += height;

		if (yOffset > 1200) {
			nextPage();
		}
	}

	private boolean check(AccountsStatement extrait) {
		boolean match = true;
		for (String accountName : extrait.getAccounts().keySet()) {
			Account c = extrait.getAccounts().get(accountName);
			BigDecimal solde = c.getInitialBalance();
			for (AccountLine l : c.getLines()) {
				if (!l.getDebit().isEmpty()) {
					solde = solde.subtract(new BigDecimal(l.getDebit()));
				} else if (!l.getCredit().isEmpty()) {
					solde = solde.add(new BigDecimal(l.getCredit()));
				}
			}
			if (solde.compareTo(c.getFinalBalance()) != 0) {
				System.err.println("Incohérence sur le compte " + accountName + ", computed = " + solde);
				System.err.println(c);
				match = false;
			}
		}

		return match;
	}

	private void nextPage() {
		if (page < allPages.size() - 1) {
			page++;
			currPage = allPages.get(page);
			yOffset = 50;
		} else {
			finished = true;
		}
	}

	private void close() throws IOException {
		doc.close();
	}
}
