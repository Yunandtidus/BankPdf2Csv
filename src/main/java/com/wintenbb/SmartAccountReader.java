package com.wintenbb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wintenbb.bankManager.domain.AccountsStatement;
import com.wintenbb.bankManager.export.csv.CSVExporter;
import com.wintenbb.bankManager.parser.pdf.PDFParser;
import com.wintenbb.bankManager.parser.pdf.config.PDFParserConfig;
import com.wintenbb.bankManager.properties.PropertyManager;

public class SmartAccountReader {

	public static final Pattern FILE_DATE = Pattern.compile(".*(\\d{4}-\\d{2}-\\d{2}(-\\d{1})?)\\.pdf");
	public static final Pattern SITUATION_OTHER_ACCOUNT = Pattern.compile("(\\d{8,12}) *(.*) * EUR *(.*)");

	private String date;

	public SmartAccountReader(String date) {
		this.date = date;
	}

	public static void main(String[] args) {
		SmartAccountReader reader = null;
		try {
			int count = 0;
			System.out.println("SmartAccountReader running ");
			for (File file : new File(PropertyManager.INPUT_FOLDER).listFiles()) {
				Matcher m = FILE_DATE.matcher(file.getName());
				if (m.find()) {
					String date = m.group(1);
					if (!new File(PropertyManager.OUTPUT_FOLDER + date + ".csv").exists()) {
						reader = new SmartAccountReader(date);
						System.out.println("Treating " + file.getName());
						AccountsStatement extrait = new PDFParser(new FileInputStream(file), PDFParserConfig.CIC)
								.treat();
						if (extrait != null) {
							new CSVExporter().export(extrait, date);
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

}
