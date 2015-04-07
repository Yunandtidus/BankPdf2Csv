package com.wintenbb;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

public class MainAccountReader {
	public static void main(String[] args) {
		try {
			PDFParser parser = new PDFParser(
					new FileInputStream(
							"C:/Users/Yunandtidus/Desktop/Travail/Comptes/Extrait de comptes 30087 33101 000376892.. au 2014-12-03.pdf"));
			parser.parse();
			PDDocument doc = parser.getPDDocument();

			boolean hasNext = true;
			int page = 0;
			int line = 0;
			boolean isEnd = false;

			BigDecimal soldeFinal = null;
			BigDecimal soldeInitial = new BigDecimal(readSoldeInitial(doc));
			BigDecimal tmp = soldeInitial;

			while (!isEnd) {
				PageConfig pc = new ParserConfig().configs.get(page == 0 ? 0
						: (page % 2 == 0 ? 2 : 1));
				List allPages = doc.getDocumentCatalog().getAllPages();
				PDPage currPage = (PDPage) allPages.get(page);
				hasNext = true;
				line = 0;
				while (hasNext) {
					int xOffset = pc.xOffset;
					PDFTextStripperByArea stripper = new PDFTextStripperByArea();
					stripper.setSortByPosition(true);
					for (int i = 0; i < pc.widths.length; i++) {
						Rectangle rect1 = new Rectangle(xOffset,
								(pc.lineHeight) * line + pc.yOffset,
								pc.widths[i], pc.lineHeight);
						stripper.addRegion("page" + page + "col" + i, rect1);
						xOffset += pc.widths[i];
					}
					stripper.extractRegions(currPage);
					Line l = new Line();
					l.setBeginDate(stripper.getTextForRegion("page" + page
							+ "col0"));
					l.setValueDate(stripper.getTextForRegion("page" + page
							+ "col1"));
					l.setLabel(stripper
							.getTextForRegion("page" + page + "col2"));
					l.setDebit(stripper
							.getTextForRegion("page" + page + "col3"));
					l.setCredit(stripper.getTextForRegion("page" + page
							+ "col4"));

					line++;

					isEnd = l.isEnd();
					hasNext = !l.isEmpty() && !l.isEnd();

					if (isEnd) {
						soldeFinal = new BigDecimal(l.getCredit());
						continue;
					} else if (!l.getDebit().isEmpty()) {
						tmp = tmp.subtract(new BigDecimal(l.getDebit()));
					} else if (!l.getCredit().isEmpty()) {
						tmp = tmp.add(new BigDecimal(l.getCredit()));
					}

					System.out.format("line %2d : ", line);
					if (hasNext) {
						System.out.println(l);
					} else {
						System.out.println();
					}
				}
				System.out.println("nextPage");
				page++;
			}
			System.out.println("done");

			System.out.println("initial = " + readSoldeInitial(doc));
			System.out.println("tmp = " + tmp);
			System.out.println("Exact match " + tmp.equals(soldeFinal));
			System.out.println("final = " + soldeFinal);
			doc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String readSoldeInitial(PDDocument doc) throws IOException {
		List allPages = doc.getDocumentCatalog().getAllPages();
		PDPage currPage = (PDPage) allPages.get(0);
		PDFTextStripperByArea stripper = new PDFTextStripperByArea();
		stripper.setSortByPosition(true);
		Rectangle rect1 = new Rectangle(460, 370, 100, 20);
		stripper.addRegion("val", rect1);
		stripper.extractRegions(currPage);
		String ret = stripper.getTextForRegion("val")
				.replaceAll("[\\s\r\n\\.]", "").replace(",", ".");
		return ret;
	}
}
