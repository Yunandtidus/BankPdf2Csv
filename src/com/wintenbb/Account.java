package com.wintenbb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Compte {

	private String accountName = null;
	private String accountNumber = null;
	private BigDecimal soldeInitial = null;
	private BigDecimal soldeFinal = null;
	private List<Line> lines = new ArrayList<Line>();

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public BigDecimal getSoldeInitial() {
		return soldeInitial;
	}

	public void setSoldeInitial(BigDecimal soldeInitial) {
		this.soldeInitial = soldeInitial;
	}

	public BigDecimal getSoldeFinal() {
		return soldeFinal;
	}

	public void setSoldeFinal(BigDecimal soldeFinal) {
		this.soldeFinal = soldeFinal;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	public void addLine(Line l) {
		if (l != null && !l.isEmpty()) {
			lines.add(l);
		}
	}

	public Line getLastLine() {
		return lines.get(lines.size() - 1);
	}

	@Override
	public String toString() {
		return String
				.format("Compte [soldeInitial = %s, soldeFinal = %s, lines = \n %s]\n",
						soldeInitial, soldeFinal, lines);
	}
}
