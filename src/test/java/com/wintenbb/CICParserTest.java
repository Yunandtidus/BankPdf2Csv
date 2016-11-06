package com.wintenbb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.wintenbb.bankManager.domain.Account;
import com.wintenbb.bankManager.domain.AccountsStatement;
import com.wintenbb.bankManager.parser.pdf.PDFParser;
import com.wintenbb.bankManager.parser.pdf.config.PDFParserConfig;

public class CICParserTest {
	private static final String FILENAME = "Extrait de comptes Anonyme XXXXXX.. au 2012-10-03.pdf";

	@Test
	public void test() throws FileNotFoundException, IOException {
		AccountsStatement extrait;
		extrait = new PDFParser(CICParserTest.class.getClassLoader().getResourceAsStream(FILENAME), PDFParserConfig.CIC)
				.treat();

		Assert.assertTrue(Pattern.matches("(\\s*<<Suite au verso>>)", " <<Suite au verso>>"));

		Assert.assertNotNull(extrait);
		Assert.assertEquals(3, extrait.getAccounts().size());

		Account account = extrait.getAccounts().get("00099999902");
		Assert.assertEquals(23, account.getLines().size());

		Assert.assertEquals("AAAAAAAA AAAAAAAA AAAAA CARTE 99999999", account.getLines().get(0).getFullLabel());
		Assert.assertEquals("PAIEMENT CB 9999 DDDDDD MCDONALD’S CARTE 99999999",
				account.getLines().get(3).getFullLabel());
		Assert.assertEquals("LLLLLLLLLLLLLLLLLL REF 99999999", account.getLines().get(19).getFullLabel());
		Assert.assertEquals("MMMMMMMMM 99999999", account.getLines().get(20).getFullLabel());
	}

}
