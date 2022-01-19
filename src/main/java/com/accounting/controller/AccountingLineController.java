package com.accounting.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.accounting.model.AccountingLineResponseWrapper;
import com.accounting.service.AccountingLineService;

/**
 * @author wilfred
 * 
 * This API class processes the uploaded Accounting line XML file payload.
 * Accordingly, it also servers the clients the returning the accounting lines
 * stored in the DB.
 * 
 * This API returns the response data wrapped by AccountingLineResponseWrapper 
 * object. This is to standardized the response which also returns the status and
 * the description message to the requesting clients.
 *
 */

@RestController
public class AccountingLineController {

	private static final Logger logger = LoggerFactory.getLogger(AccountingLineController.class);

	@Autowired
	private AccountingLineService service;
	
	
	/**
	 * This method processes the incoming Accounting Line
	 * XML file. The processed
	 * are stored in memory.
	 * 
	 * If the input file was already processed previously,
	 * then it will return an return an error message in the
	 * response
	 * 
	 * 
	 * @param inputFile		Incoming XML file
	 * @return				AccountingLineResponseWrapper object
	 * @throws IOException
	 */
	@PostMapping("/processAccountingLine")
	public <T> ResponseEntity<AccountingLineResponseWrapper<T>> processInputFile(
					@RequestParam("xmlfile") MultipartFile inputFile) throws IOException {
		logger.info("START - processInputFile()");
		AccountingLineResponseWrapper<T> result = service.process(inputFile.getInputStream());
		logger.info("END - processInputFile()");
		return ResponseEntity.ok(result);
	}
	
	/**
	 * This method returns the object containing the 
	 * previously processed accounting lines
	 * 
	 * @return		AccountingLineResponseWrapper object 
	 */
	@GetMapping("/getAccountingLines")
	public <T> ResponseEntity<AccountingLineResponseWrapper<T>> getAccountingLines() {
		logger.info("START - getAccountingLines()");
		AccountingLineResponseWrapper<T> result  = service.getAccountingLines();
		logger.info("END - getAccountingLines()");
		return ResponseEntity.ok(result);
	}
}
