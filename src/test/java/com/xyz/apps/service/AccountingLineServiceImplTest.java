package com.xyz.apps.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.support.SimpleValueWrapper;

import com.accounting.builder.AccountingLineBuilder;
import com.accounting.cache.AppCacheConfig;
import com.accounting.constants.Constants;
import com.accounting.model.AccountingLineResponseWrapper;
import com.accounting.model.AccountingLineX;
import com.accounting.model.to.AccountingLine;
import com.accounting.service.AccountingLineServiceImpl;
import com.accounting.util.AccountingLineUtil;

@RunWith(MockitoJUnitRunner.class)
public class AccountingLineServiceImplTest {

	private AccountingLineX accountingLineX;
	
	private InputStream inputStream;
	
	private ValueWrapper valueWrapper;
	
	private AccountingLineResponseWrapper<String> wrapper;
	
	@InjectMocks
	private AccountingLineServiceImpl service;
	
	@Mock
	private AppCacheConfig caching;
	
	
	@Test
	public void testGetAccountingLines() {
		when(caching.getAccountingLineCache(Constants.CacheKey.ACCOUNTING_LINE_CACHE_KEY.value)).thenReturn(valueWrapper);
		AccountingLineResponseWrapper<Map<String, AccountingLine>> wrapValues = service.getAccountingLines();
		List<AccountingLine> values = (List<AccountingLine>) wrapValues.getData();
		assertNotNull(values);
		AccountingLine acctLine = values.get(0);
		assertTrue("3889081143".equals(acctLine.getReceiptNumber()));
	}
	
	@Test
	public void testProcess() throws JAXBException, IOException {
		
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			
			theMock.when(() -> AccountingLineUtil.parseXML(inputStream)).thenReturn(accountingLineX);
			theMock.when(() -> AccountingLineUtil.getStrAmt(any(String.class))).thenReturn("1252.00");
			theMock.when(() -> AccountingLineUtil.getKey(any(AccountingLine.class))).thenReturn("the_key");
			theMock.when(() -> AccountingLineUtil.wrapResponse(any(String.class), any(String.class), any(String.class))).thenReturn(wrapper);
			AccountingLineResponseWrapper<Map<String, AccountingLine>> wrapValues = service.process(inputStream);
			assertTrue("SUCCESS".equals(wrapValues.getStatus()));
		}
	}
	
	@Before
	public void init() throws FileNotFoundException, JAXBException {
		File xmlFile = new File("src/test/resources/AccountingLine.xml");
		inputStream = new FileInputStream(xmlFile);
		
		JAXBContext context = JAXBContext.newInstance(AccountingLineX.class);
		accountingLineX = (AccountingLineX) context.createUnmarshaller()
		      .unmarshal( inputStream );
		
		AccountingLine acctLine = new AccountingLineBuilder().builder(acctLineBuilder -> 
		acctLineBuilder.setAccountingLineX(accountingLineX)
	      ).build();
		
		Map<String, AccountingLine> values = new HashMap<>();
		values.put("ac_key", acctLine);
		
		valueWrapper = new SimpleValueWrapper(values);
		
		 
		wrapper = new AccountingLineResponseWrapper<>();
		wrapper.setData("");
		wrapper.setStatus(Constants.Message.SUCCESS.value);
		wrapper.setMessage(Constants.Message.SUCCESS_MSG.value);
	}
	
}
