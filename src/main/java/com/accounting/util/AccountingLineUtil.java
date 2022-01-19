package com.accounting.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.accounting.model.AccountingLineResponseWrapper;
import com.accounting.model.AccountingLineX;
import com.accounting.model.to.AccountingLine;


/**
 * 
 * @author wilfred
 *
 * This is a utility class with the following functionality
 * - parsing of xml
 * - wrapping the response
 * - triming the monetary string
 * - getting the number of decimals from a moneytary string
 * - creating a key to be used for caching.
 */
public class AccountingLineUtil {

	private AccountingLineUtil() {}
	
	public static AccountingLineX parseXML(InputStream inputStream) throws JAXBException, IOException {
		 AccountingLineX x = null;
			 JAXBContext context = JAXBContext.newInstance(AccountingLineX.class);
			x = (AccountingLineX) context.createUnmarshaller()
			      .unmarshal( inputStream );

		 return x;
	}
	
	public static <T> AccountingLineResponseWrapper<T> wrapResponse(T data, String status, String message) {
		AccountingLineResponseWrapper<T> wrapper = new AccountingLineResponseWrapper<>();
		wrapper.setData(data);
		wrapper.setStatus(status);
		wrapper.setMessage(message);

		return wrapper;
	}
	
	public static String getStrAmt(String amt) {
		return amt != null ? amt.trim() : "0";
	}
	
	public static Integer getNoOfDecimals(String num) {
		return (num.length() - num.lastIndexOf(".")) - 1;
	}
	
	public static String getKey(AccountingLine line) {
		return String.join("_", line.getAccountingLineID(),
				line.getElementNumber(),
				line.getTypeIndicator(),
				line.getAccountingVendorCode(),
				line.getReceiptNumber(),
				line.getNumberOfConjunctedDocuments(),
				line.getTravelerName(),
				line.getOriginalTicketNumber(),
				line.getInvoiceNumber());
	}
	
}
