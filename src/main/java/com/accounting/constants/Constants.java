package com.accounting.constants;

public class Constants {

	public static final String USD = "USD";
	
	public enum CacheKey {
		ACCOUNTING_LINE_CACHE_KEY("ACCOUNTING_LINE_CACHE_KEY");
		
		public final String value;
		
		private CacheKey(String value) {
			this.value = value;
		}	
	}
	
	public enum Message {
		ERROR("ERROR"),
		ERROR_MSG_XML_PARSING("An error was encountered while parsing XML payload."),
		ERROR_MSG_RETRIEVING_ACCOUNTING_LINE("An error was encountered while retrieving list of Accounting Lines."),
		ERROR_MSG_ACCTLINE_EXISTS("The accounting line payload already exists in the database."),
		SUCCESS("SUCCESS"),
		SUCCESS_MSG("Request was successfully processed. Response was returned."),
		SUCCESS_MSG_PARSE_XML("XML Payload was successfully processed.");
		

		public final String value;
		
		private Message(String value) {
			this.value = value;
		}
	}
	
}
