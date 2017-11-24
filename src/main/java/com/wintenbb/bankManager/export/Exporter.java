package com.wintenbb.bankManager.export;

import java.io.IOException;

import com.wintenbb.bankManager.domain.AccountsStatement;

public interface Exporter {
	void export(AccountsStatement accounts, String date) throws IOException;
}
