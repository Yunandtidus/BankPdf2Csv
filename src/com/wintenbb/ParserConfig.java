package com.wintenbb;

import java.util.Arrays;
import java.util.List;

/**
 * Contains Bank specific Parser configuration, each ParserConfig is based on a
 * list of PageConfig. <br>
 * Page Configs First page is used for parsing the first PDF page, second and
 * first page are then used alternatively to parse respectively even and odd
 * pages.
 * 
 * @author Yunandtidus
 *
 */
public enum ParserConfig {
	CIC(Arrays.asList(
			new PageConfig(52, 394, 11, new int[] { 42, 43, 250, 80, 80 }),
			new PageConfig(61, 144, 11, new int[] { 42, 43, 250, 80, 80 }),
			new PageConfig(52, 196, 11, new int[] { 42, 43, 250, 80, 80 })
			));

	/**
	 * Page Configs First page is used for parsing the first PDF page, second
	 * and first page are then used alternatively to parse respectively even and
	 * odd pages
	 */
	public List<PageConfig> configs;

	/**
	 * Constructor
	 * 
	 * @param configs
	 */
	private ParserConfig(List<PageConfig> configs) {
		this.configs = configs;
	}

}
