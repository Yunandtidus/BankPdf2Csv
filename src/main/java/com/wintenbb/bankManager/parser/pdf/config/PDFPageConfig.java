package com.wintenbb.bankManager.parser.pdf.config;

/**
 * Pixel offset line height and columns widths in order to parse a PDF table
 * 
 * @author Yunandtidus
 *
 */
public class PDFPageConfig {
	/**
	 * the x offset
	 */
	public int xOffset;

	/**
	 * the y offset
	 */
	public int yOffset;

	/**
	 * the line's height
	 */
	public int lineHeight;

	/**
	 * the columns' width
	 */
	public int[] widths;

	/**
	 * @param xOffset
	 *            x offset
	 * @param yOffset
	 *            y offset
	 * @param lineHeight
	 *            line's height
	 * @param widths
	 *            columns' width
	 */
	public PDFPageConfig(int xOffset, int yOffset, int lineHeight, int[] widths) {
		super();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.lineHeight = lineHeight;
		this.widths = widths;
	}

}
