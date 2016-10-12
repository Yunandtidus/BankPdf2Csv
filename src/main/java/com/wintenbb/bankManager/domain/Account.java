package com.wintenbb.bankManager.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The class <code>Account</code> represents the state of an account for a given
 * PDF it contains :
 * <ul>
 * <li>the account name</li>
 * <li>the account number</li>
 * <li>the initial solde</li>
 * <li>the final solde</li>
 * <li>the lines</li>
 * </ul>
 * 
 * @author Yunandtidus
 *
 */
public class Account {

	/**
	 * The account's name
	 */
	private String accountName = null;

	/**
	 * The account's number
	 */
	private String accountNumber = null;

	/**
	 * The account's initial balance
	 */
	private BigDecimal initialBalance = null;

	/**
	 * The account's final balance
	 */
	private BigDecimal finalBalance = null;

	/**
	 * The account's lines for this PDF
	 */
	private List<AccountLine> lines = new ArrayList<AccountLine>();

	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * @param accountName
	 *            the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *            the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the soldeInitial
	 */
	public BigDecimal getInitialBalance() {
		return initialBalance;
	}

	/**
	 * @param soldeInitial
	 *            the soldeInitial to set
	 */
	public void setInitialBalance(BigDecimal initialBalance) {
		this.initialBalance = initialBalance;
	}

	/**
	 * @return the soldeFinal
	 */
	public BigDecimal getFinalBalance() {
		return finalBalance;
	}

	/**
	 * @param soldeFinal
	 *            the soldeFinal to set
	 */
	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}

	/**
	 * @return the lines
	 */
	public List<AccountLine> getLines() {
		return lines;
	}

	/**
	 * @param lines
	 *            the lines to set
	 */
	public void setLines(List<AccountLine> lines) {
		this.lines = lines;
	}

	/**
	 * Helper method for adding a line
	 * 
	 * @param l
	 */
	public void addLine(AccountLine l) {
		if (l != null && !l.isEmpty()) {
			lines.add(l);
		}
	}

	/**
	 * Helper method to get the last line
	 * 
	 * @return the last account line
	 */
	public AccountLine getLastLine() {
		return lines.get(lines.size() - 1);
	}

	@Override
	public String toString() {
		return String
				.format("Compte [soldeInitial = %s, soldeFinal = %s, lines = \n %s]\n",
						initialBalance, finalBalance, lines);
	}
}
