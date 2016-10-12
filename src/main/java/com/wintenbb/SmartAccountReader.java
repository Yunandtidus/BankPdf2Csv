package com.wintenbb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wintenbb.bankManager.domain.Account;
import com.wintenbb.bankManager.domain.AccountLine;
import com.wintenbb.bankManager.domain.AccountsStatement;
import com.wintenbb.bankManager.parser.pdf.PDFParser;
import com.wintenbb.bankManager.parser.pdf.config.PDFParserConfig;

public class SmartAccountReader {

	public static final Pattern FILE_DATE = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2}(-\\d{1})?)\\.pdf");
	public static final Pattern SITUATION_OTHER_ACCOUNT = Pattern.compile("(\\d{8,12}) *(.*) * EUR *(.*)");

	public static final String OUTPUT_FOLDER = "D:/Yunandtidus/Travail/Travail/Comptes/output/";
	public static final String INPUT_FOLDER = "D:/Yunandtidus/Travail/Travail/Comptes/input/";

	private String date;

	public SmartAccountReader(String date) {
		this.date = date;
	}

	public static void main(String[] args) {
		SmartAccountReader reader = null;
		try {
			int count = 0;
			System.out.println("SmartAccountReader running ");
			for (File file : new File(INPUT_FOLDER).listFiles()) {
				Matcher m = FILE_DATE.matcher(file.getName());
				if (m.find()) {
					String date = m.group(1);
					if (!new File(OUTPUT_FOLDER + date + ".csv").exists()) {
						reader = new SmartAccountReader(date);
						System.out.println("Treating " + file.getName());
						AccountsStatement extrait = new PDFParser(new FileInputStream(file), PDFParserConfig.CIC)
								.treat();
						if (extrait != null) {
							reader.export(extrait);
						} else {
							System.err.println("Error in " + file);
						}
						count++;
					}
				} else {
					System.err.println("parsing error filename" + file);
				}

			}
			System.out.println("SmartAccountReader treat " + count + " files");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void export(AccountsStatement extrait) throws IOException {
		FileWriter w = new FileWriter(new File(OUTPUT_FOLDER + date + ".csv"));
		System.out.println("Exporting " + date + ".csv");
		for (String accountName : extrait.getAccounts().keySet()) {
			Account c = extrait.getAccounts().get(accountName);

			w.append(c.getAccountName() + " N° " + c.getAccountNumber() + ";;;;Solde initial;" + c.getInitialBalance()
					+ "\n");

			if (!c.getLines().isEmpty()) {
				w.append("Date;Date valeur;Libellé;Débit;Crédit; \n");
			}
			for (AccountLine l : c.getLines()) {
				w.append(l.getBeginDate() + ";" + l.getValueDate() + ";" + l.getLabel() + " " + l.getLabel2() + ";"
						+ l.getDebit() + ";" + l.getCredit() + ";;" + lineType(l) + "\n");
			}
			w.append(";;;;Solde final;" + c.getFinalBalance() + "\n");

			w.append("\n");
		}
		w.close();
	}

	public String lineType(AccountLine l) {
		// return
		// ";logement;Voiture;Loisirs;Nourriture;Santé;Divers;Impots;Banque;Revenus";
		if (l.getLabel().contains("PRLV NVO ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" CENTURY 21 ")) {
			return ";Loyer;;;;;;;;";
		} else if (l.getFullLabel().contains(" EDF ")) {
			return ";Electricité;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE HAUTDEBIT ")) {
			return ";Internet;;;;;;;;";
		} else if (l.getFullLabel().contains(" FREE MOBILE")) {
			return ";Téléphone;;;;;;;;";
		} else if (l.getFullLabel().contains(" APRR ")) {
			return ";;Péage;;;;;;;";
		} else if (l.getFullLabel().contains(" PARIS RHIN RH ")) {
			return ";;Péage;;;;;;;";
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
			return ";;;;;;;;Frais banquaires;";
		} else if (l.getFullLabel().contains("ECH PRET ")) {
			return ";;;;;;;;Frais banquaires;";
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

}
