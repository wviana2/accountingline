package com.accounting.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.stereotype.Service;

import com.accounting.builder.AccountingLineBuilder;
import com.accounting.cache.AppCacheConfig;
import com.accounting.constants.Constants;
import com.accounting.model.AccountingLineResponseWrapper;
import com.accounting.model.AccountingLineX;
import com.accounting.model.to.AccountingLine;
import com.accounting.util.AccountingLineUtil;


/**
 * @author wilfred
 * 
 * This class processes the incoming Accounting line XML file payload.
 * It then convert the XYZ object processed by builder 
 * into AccountingLine object. The processed file will be stored in the
 * memory. 
 * 
 * This API returns the response data wrapped by AccountingLineResponseWrapper 
 * object. This is to standardized the response which also returns the status and
 * the description message to the requesting clients.
 *
 */
@Service
public class AccountingLineServiceImpl implements AccountingLineService {

	private static Logger logger = LoggerFactory.getLogger(AccountingLineServiceImpl.class);
	
	@Autowired
	private AppCacheConfig caching;
	
	/**
	 * This method processes the incoming 
	 * XML file. The processed
	 * are stored in memory.
	 * 
	 * If the input file was already processed previously,
	 * then it will return an return an error message in the
	 * response
	 * 
	 * @param inputStream
	 * @return				AccountingLineResponseWrapper object
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> AccountingLineResponseWrapper<T> process(InputStream inputStream) {
		try {
			AccountingLineX acctLineX = AccountingLineUtil.parseXML(inputStream);

			AccountingLine acctLine = new AccountingLineBuilder().builder(acctLineBuilder -> 
				acctLineBuilder.setAccountingLineX(acctLineX)).build();

			ValueWrapper wrapper = caching.getAccountingLineCache(Constants.CacheKey.ACCOUNTING_LINE_CACHE_KEY.value);
			Map<String,AccountingLine> previousValue = null;
			String key = AccountingLineUtil.getKey(acctLine);
			
			if (wrapper != null) {
				previousValue = (Map<String, AccountingLine>) wrapper.get();
				if (previousValue != null) {
					if(previousValue.get(key) != null) {
						return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.ERROR.value, Constants.Message.ERROR_MSG_ACCTLINE_EXISTS.value);
					} else {
						previousValue.put(key,acctLine);
					}
				} 
			} else {
				previousValue = new HashMap<>();
				previousValue.put(key,acctLine);
			}
			caching.putAccountingLineCache(Constants.CacheKey.ACCOUNTING_LINE_CACHE_KEY.value, previousValue);
		} catch (JAXBException e) {
			logger.error("JAXBException thrown: ", e.getMessage(), e);
			return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.ERROR.value, Constants.Message.ERROR_MSG_XML_PARSING.value);
		} catch (IOException e) {
			logger.error("IOException thrown: ", e.getMessage(), e);
			return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.ERROR.value, Constants.Message.ERROR_MSG_XML_PARSING.value);
		}
		return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.SUCCESS.value, Constants.Message.SUCCESS_MSG_PARSE_XML.value);
	}

	/**
	 * This method returns the object containing the 
	 * previously processed accounting lines.
	 * 
	 * @return		AccountingLineResponseWrapper object 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> AccountingLineResponseWrapper<T> getAccountingLines() {
		try {
			ValueWrapper wrapper = caching.getAccountingLineCache(Constants.CacheKey.ACCOUNTING_LINE_CACHE_KEY.value);
			List<AccountingLine> acctLineList = new ArrayList<>();
			
			if (wrapper != null) {
				Map<String,AccountingLine> values = (Map<String,AccountingLine>) wrapper.get();
				values.forEach((k,v) -> {
					acctLineList.add(v);
				});
				
				return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse(acctLineList,
						Constants.Message.SUCCESS.value, Constants.Message.SUCCESS_MSG.value);
			}
		} catch (Exception e) {
			return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.ERROR.value,
					Constants.Message.ERROR_MSG_RETRIEVING_ACCOUNTING_LINE.value);
		}

		return (AccountingLineResponseWrapper<T>) AccountingLineUtil.wrapResponse("", Constants.Message.SUCCESS.value,
				Constants.Message.SUCCESS_MSG.value);
	}

}
