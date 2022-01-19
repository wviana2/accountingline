package com.accounting.service;

import java.io.InputStream;

import com.accounting.model.AccountingLineResponseWrapper;

public interface AccountingLineService {

	<T> AccountingLineResponseWrapper<T> process(InputStream inputStream);
	
	<T> AccountingLineResponseWrapper<T> getAccountingLines();
}
