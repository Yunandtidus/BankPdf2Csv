package com.wintenbb.bankManager.export.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.wintenbb.bankManager.business.Organizer;
import com.wintenbb.bankManager.domain.Account;
import com.wintenbb.bankManager.domain.AccountLine;
import com.wintenbb.bankManager.domain.AccountsStatement;
import com.wintenbb.bankManager.export.Exporter;
import com.wintenbb.bankManager.properties.PropertyManager;

public class CSVExporter implements Exporter {

	public void export(AccountsStatement extrait, String date) throws IOException {
		FileWriter w = new FileWriter(new File(PropertyManager.OUTPUT_FOLDER + date + ".csv"));
		System.out.println("Exporting " + date + ".csv");
		for (String accountName : extrait.getAccounts().keySet()) {
			Account c = extrait.getAccounts().get(accountName);
			System.out.println(c.getAccountName());

			w.append(c.getAccountName() + " N° " + c.getAccountNumber() + ";;;;Solde initial;" + c.getInitialBalance()
					+ "\n");

			if (!c.getLines().isEmpty()) {
				w.append("Date;Date valeur;Libellé;Débit;Crédit; \n");
			}
			for (AccountLine l : c.getLines()) {
				w.append(l.getBeginDate() + ";" + l.getValueDate() + ";" + l.getLabel() + " " + l.getLabel2() + ";"
						+ l.getDebit() + ";" + l.getCredit() + ";;" + Organizer.getInstance().lineType(l) + "\n");
			}
			w.append(";;;;Solde final;" + c.getFinalBalance() + "\n");

			w.append("\n");
		}
		w.close();
	}

}
