package com.wintenbb;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class SmartAccountReader {

	public static final Pattern FILE_DATE = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2})\\.pdf");
	public static final Pattern ACCOUNT_PATTERN = Pattern.compile("([\\w\\s]+) N� (\\d+)");
	public static final Pattern LINE_PATTERN = Pattern.compile("^(\\d{2}/\\d{2}/\\d{4}) .*");
	public static final Pattern SOLDE_NUL_PATTERN = Pattern.compile("SOLDE NUL");
	public static final Pattern SOLDE_CREDITEUR_PATTERN = Pattern.compile("SOLDE CREDITEUR .*/\\d{4} *([-\\.,\\d]+)");
	public static final Pattern LINE_COMPLEMENTARY_PATTERN = Pattern.compile(".+");
	public static final Pattern SITUATION_OTHER_ACCOUNT = Pattern.compile("(\\d{8,12}) *(.*) * EUR *(.*)");

	public static final String OUTPUT_FOLDER = "C:/Users/Yunandtidus/Desktop/Travail/Comptes/output/";
	public static final String INPUT_FOLDER = "C:/Users/Yunandtidus/Desktop/Travail/Comptes/input/";

	public static void main(String[] args) {
		SmartAccountReader reader = null;
		try {
			for (File file : new File(INPUT_FOLDER).listFiles()) {
				Matcher m = FILE_DATE.matcher(file.getName());
				if (m.find()) {
					String date = m.group(1);
					if (!new File(OUTPUT_FOLDER + date + ".csv").exists()) {
						reader = new SmartAccountReader(file, date);
						System.out.println("Treating " + file.getName());
						if (reader.treat()) {
							reader.export();
						} else {
							System.err.println("Error in " + file);
						}
					}
				} else {
					System.err.println("parsing error filename" + file);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void export() throws IOException {
		FileWriter w = new FileWriter(new File(OUTPUT_FOLDER + date + ".csv"));
		System.out.println("Exporting " + date + ".csv");
		for (String accountName : extrait.getComptes().keySet()) {
			Compte c = extrait.getComptes().get(accountName);

			w.append(c.getAccountName() + " N� " + c.getAccountNumber() + ";;;;Solde initial;" + c.getSoldeInitial()
					+ "\n");

			if (!c.getLines().isEmpty()) {
				w.append("Date;Date valeur;Libell�;D�bit;Cr�dit; \n");
			}
			for (Line l : c.getLines()) {
				w.append(l.getBeginDate() + ";" + l.getValueDate() + ";" + l.getLabel() + " " + l.getLabel2() + ";"
						+ l.getDebit() + ";" + l.getCredit() + ";;" + lineType(l) + "\n");
			}
			w.append(";;;;Solde final;" + c.getSoldeFinal() + "\n");

			w.append("\n");
		}
		w.close();
	}

	public String lineType(Line l) {
		// return
		// ";logement;Voiture;Loisirs;Nourriture;Sant�;Divers;Impots;Banque;Revenus";
		if (l.getLabel().contains("PRLV NVO ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" CENTURY 21 ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" EDF ")) {
			return ";Electricit�;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE HAUTDEBIT ")) {
			return ";Internet;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE MOBILE")) {
			return ";T�l�phone;;;;;;;;";
		} else if (l.getFullLabel().contains(" APRR ")) {
			return ";;P�age;;;;;;;";
		} else if (l.getFullLabel().contains(" PARIS RHIN RH ")) {
			return ";;P�age;;;;;;;";
		} else if (l.getFullLabel().contains(" S/S ")) {
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains(" CARBURANT ")) {
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains(" DAC ")) {
			// distributeur automatique de carburant
			return ";;Essence;;;;;;;";
		} else if (l.getFullLabel().contains("RETRAIT DAB ")) {
			return ";;;;;;Autres;;;";
		} else if (l.getFullLabel().contains(" PARADISO ")) {
			return ";;;;Restaurant;;;;;";
		} else if (l.getFullLabel().contains(" CLAFOUTIS ")) {
			return ";;;;Restaurant;;;;;";
		} else if (l.getFullLabel().contains(" IMPOT ")) {
			return ";;;;;;;Impots;;";
		} else if (l.getFullLabel().contains("F COTIS ")) {
			return ";;;;;;;;Banque;";
		} else if (l.getFullLabel().contains("ECH PRET ")) {
			return ";;;;;;;;Banque;";
		} else if (l.getFullLabel().contains(" SYSBELF ")) {
			return ";;;;;;;;;Paye";
		} else if (l.getFullLabel().contains(" DECATHLON ")) {
			return ";;;Sport;;;;;;";
		} else if (l.getFullLabel().contains(" AUCHAN ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains("LECLERC ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains(" SAS BELFI")) {
			return ";;;Courses;;;;;;";
		} else if (l.getFullLabel().contains(" INTERMARCHE ")) {
			return ";;;;Courses;;;;;";
		} else if (l.getFullLabel().contains(" CORA ")) {
			return ";;;;Courses;;;;;";
		}
		return "";
	}

	public boolean treat() throws IOException {
		try {

			extrait = new Extrait();
			Compte currAccount = null;
			String currentAccountName = null;
			boolean lastReadWasLine = false;
			while (!finished) {
				readNextLine();
				while (lastLine == null && !finished) {
					readNextLine();
				}
				if (finished) {
					break;
				}
				// System.out.println("->" + lastLine);

				Matcher m;
				if ((m = ACCOUNT_PATTERN.matcher(lastLine)).find()) {
					currentAccountName = m.group(2).trim();
					if (!extrait.getComptes().containsKey(currentAccountName)) {
						currAccount = new Compte();
						currAccount.setAccountName(m.group(1));
						currAccount.setAccountNumber(currentAccountName);
						extrait.getComptes().put(currentAccountName, currAccount);
					} else {
						currAccount = extrait.getComptes().get(currentAccountName);
					}
					lastReadWasLine = false;
				} else if ((m = SOLDE_CREDITEUR_PATTERN.matcher(lastLine)).find()) {
					if (currAccount.getSoldeInitial() == null) {
						currAccount.setSoldeInitial(new BigDecimal(m.group(1).replaceAll("\\.", "")
								.replace(",", ".")));
					} else {
						currAccount.setSoldeFinal(new BigDecimal(m.group(1).replaceAll("\\.", "")
								.replace(",", ".")));
					}
					lastReadWasLine = false;
				} else if ((m = SOLDE_NUL_PATTERN.matcher(lastLine)).find()) {
					if (currAccount.getSoldeInitial() == null) {
						currAccount.setSoldeInitial(BigDecimal.ZERO);
					} else {
						currAccount.setSoldeFinal(BigDecimal.ZERO);
					}
					lastReadWasLine = false;
				} else if ((m = SITUATION_OTHER_ACCOUNT.matcher(lastLine)).find()) {
					currentAccountName = m.group(1).trim();
					if (!extrait.getComptes().containsKey(currentAccountName)) {
						currAccount = new Compte();
						currAccount.setAccountNumber(currentAccountName);
						currAccount.setAccountName(m.group(2));
						extrait.getComptes().put(currentAccountName, currAccount);
					}
					System.out.println(m.group(3));
					BigDecimal solde = new BigDecimal(m.group(3).replaceAll("\\.", "")
							.replace(",", "."));
					currAccount.setSoldeInitial(solde);
					currAccount.setSoldeFinal(solde);
					lastReadWasLine = false;
				} else if ((m = LINE_PATTERN.matcher(lastLine)).find()) {
					currAccount.addLine(parseLine());
					lastReadWasLine = true;
				} else if ((m = LINE_COMPLEMENTARY_PATTERN.matcher(lastLine)).find() && lastReadWasLine) {
					currAccount.getLastLine().setLabel2(m.group());
					lastReadWasLine = false;
				}
			}

			return check(extrait);
		} finally {
			close();
		}
	}

	public SmartAccountReader(File file, String date) throws FileNotFoundException, IOException {
		this.date = date;
		PDFParser parser = new PDFParser(new FileInputStream(file));
		parser.parse();
		doc = parser.getPDDocument();

		allPages = doc.getDocumentCatalog().getAllPages();
		currPage = allPages.get(page);

		parserConf = new ParserConfig();
	}

	Extrait extrait;
	String date;
	ParserConfig parserConf;
	PDDocument doc;
	List<PDPage> allPages;
	PDPage currPage;
	int lastHeight = 0;
	int lastPage = 0;
	int page = 0;
	int lastOffset = 0;
	int yOffset = 50;
	String readText = null;
	String lastLine = null;

	boolean finished = false;

	public Line parseLine() throws IOException {
		PageConfig pc = parserConf.configs.get(lastPage % 2);
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		int xOffset = pc.xOffset;
		for (int i = 0; i < pc.widths.length; i++) {
			Rectangle rect1 = new Rectangle(xOffset, lastOffset, pc.widths[i], lastHeight);
			stripper.addRegion("page" + page + "col" + i, rect1);
			xOffset += pc.widths[i];
		}
		stripper.extractRegions(currPage);
		Line l = new Line();
		l.setBeginDate(stripper.getTextForRegion("page" + page + "col0"));
		l.setValueDate(stripper.getTextForRegion("page" + page + "col1"));
		l.setLabel(stripper
				.getTextForRegion("page" + page + "col2"));
		l.setDebit(stripper
				.getTextForRegion("page" + page + "col3"));
		l.setCredit(stripper.getTextForRegion("page" + page + "col4"));
		return l;
	}

	public void readNextLine() throws IOException {
		int height = 1;
		while ((readText == null || readText.matches("\r\n"))
				&& height + yOffset < 1200) {
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
			if (page < allPages.size() - 1) {
				page++;
				currPage = allPages.get(page);
				yOffset = 50;
			} else {
				finished = true;
			}
		}
	}

	public boolean check(Extrait extrait) {
		boolean match = true;
		for (String accountName : extrait.getComptes().keySet()) {
			Compte c = extrait.getComptes().get(accountName);
			BigDecimal solde = c.getSoldeInitial();
			for (Line l : c.getLines()) {
				if (!l.getDebit().isEmpty()) {
					solde = solde.subtract(new BigDecimal(l.getDebit()));
				} else if (!l.getCredit().isEmpty()) {
					solde = solde.add(new BigDecimal(l.getCredit()));
				}
			}
			if (solde.compareTo(c.getSoldeFinal()) != 0) {
				System.err.println("Incoh�rence sur le compte " + accountName + ", computed = " + solde);
				System.err.println(c);
				match = false;
			}
		}

		return match;
	}

	public void close() throws IOException {
		doc.close();
	}
}