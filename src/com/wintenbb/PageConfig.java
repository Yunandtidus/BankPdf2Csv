package com.wintenbb;

/**
 * Pixel offset line height and columns widths in order to parse a PDF table
 * 
 * @author Yunandtidus
 *
 */
public class PageConfig {
	/**
	 * the x offset
	 */
	public int xOffset;

	/**
	 * the y offset
	 */
	public int yOffset;

	/**
	 * the line height
	 */
	public int lineHeight;

	/**
	 * the column width
	 */
	public int[] widths;

	/**
	 * @param xOffset
	 * @param yOffset
	 * @param lineHeight
	 * @param widths
	 */
	public PageConfig(int xOffset, int yOffset, int lineHeight, int[] widths) {
		super();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.lineHeight = lineHeight;
		this.widths = widths;
	}

}
