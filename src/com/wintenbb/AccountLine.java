package com.wintenbb;

public class Line {
	private String beginDate;
	private String valueDate;
	private String label;
	private String label2;
	private String debit;
	private String credit;

	@Override
	public String toString() {
		return String
				.format("Line [beginDate = %10s, valueDate = %10s, label = %30s # %30s, debit = %10s, credit = %10s]\n",
						beginDate, valueDate, label, label2, debit, credit);
	}

	public boolean isEmpty() {
		return beginDate.isEmpty() && valueDate.isEmpty() && label.isEmpty();
	}

	public boolean isEnd() {
		return label.contains("SOLDE CREDITEUR");
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = simplify(beginDate);
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = simplify(valueDate);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = simplify(label);
	}

	public String getDebit() {
		return getValueForParse(debit);
	}

	public void setDebit(String debit) {
		this.debit = simplify(debit);
	}

	public String getCredit() {
		return getValueForParse(credit);
	}

	public void setCredit(String credit) {
		this.credit = simplify(credit);
	}

	public String getLabel2() {
		return simplify(label2);
	}

	public String getFullLabel() {
		return getLabel() + " " + getLabel2();
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	private String simplify(String s) {
		return s == null ? "" : s.replaceAll("[\r\n]", "").replaceAll("\\s+", " ");
	}

	private String getValueForParse(String s) {
		return s.replaceAll("[\\s\r\n\\.]", "").replace(",", ".");
	}
}
