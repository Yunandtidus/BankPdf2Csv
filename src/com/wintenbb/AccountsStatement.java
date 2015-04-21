package com.wintenbb;

import java.util.Map;
import java.util.TreeMap;

/**
 * This is the class containing all accounts defined in the PDF
 * 
 * @author Yunandtidus
 *
 */
public class AccountsStatement {

	/**
	 * The accounts
	 */
	private Map<String, Account> accounts = new TreeMap<String, Account>();

	/**
	 * 
	 * @return the accounts
	 */
	public Map<String, Account> getAccounts() {
		return accounts;
	}

	/**
	 * 
	 * @param accounts
	 */
	public void setAccounts(Map<String, Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return accounts.toString();
	}
}
