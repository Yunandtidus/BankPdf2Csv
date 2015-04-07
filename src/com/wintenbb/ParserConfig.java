package com.wintenbb;

import java.util.ArrayList;
import java.util.List;

public class ParserConfig {
	public List<PageConfig> configs;

	public ParserConfig() {
		configs = new ArrayList<PageConfig>();

		PageConfig pc = new PageConfig();
		pc.xOffset = 52;
		pc.yOffset = 394;
		pc.lineHeight = 11;
		pc.widths = new int[] { 42, 43, 250, 80, 80 };

		configs.add(pc);

		pc = new PageConfig();
		pc.xOffset = 61;
		pc.yOffset = 144;
		pc.lineHeight = 11;
		pc.widths = new int[] { 42, 43, 250, 80, 80 };
		configs.add(pc);

		pc = new PageConfig();
		pc.xOffset = 52;
		pc.yOffset = 196;
		pc.lineHeight = 11;
		pc.widths = new int[] { 42, 43, 250, 80, 80 };
		configs.add(pc);
	}
}
