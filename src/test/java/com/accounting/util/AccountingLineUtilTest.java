package com.accounting.util;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.accounting.builder.AccountingLineBuilder;
import com.accounting.constants.Constants;
import com.accounting.model.AccountingLineResponseWrapper;
import com.accounting.model.AccountingLineX;
import com.accounting.model.to.AccountingLine;

@RunWith(MockitoJUnitRunner.class)
public class AccountingLineUtilTest {

	private AccountingLineX accountingLineX;
	
	private InputStream inputStream;
	
	private AccountingLine acctLine;
	
	private AccountingLineResponseWrapper<String> wrapper;
	
	@Test
	public void testParseXML() throws JAXBException, IOException {
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			theMock.when(() -> AccountingLineUtil.parseXML(inputStream)).thenReturn(accountingLineX);
			AccountingLineX alx = AccountingLineUtil.parseXML(inputStream);
			assertTrue("123456789".equals(alx.getCreditCardNumber()));
		}
	}
	
	@Test
	public void testWrapResponse() throws JAXBException, IOException {
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			theMock.when(() -> AccountingLineUtil.wrapResponse(any(String.class), any(String.class), any(String.class))).thenReturn(wrapper);
			AccountingLineResponseWrapper<String> wrapper = AccountingLineUtil.wrapResponse("", Constants.Message.SUCCESS.value, Constants.Message.SUCCESS_MSG.value);
			assertTrue("SUCCESS".equals(wrapper.getStatus()));
		}
	}
	
	@Test
	public void testGetStrAmt() throws JAXBException, IOException {
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			theMock.when(() -> AccountingLineUtil.getStrAmt(any(String.class))).thenReturn("0");
			String s = AccountingLineUtil.getStrAmt("");
			assertTrue("0".equals(s));
		}
	}
	
	@Test
	public void testGetNoOfDecimals() throws JAXBException, IOException {
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			theMock.when(() -> AccountingLineUtil.getNoOfDecimals(any(String.class))).thenReturn(new Integer(2));
			Integer s = AccountingLineUtil.getNoOfDecimals("12345.56");
			assertTrue(Integer.valueOf("2").equals(s));
		}
	}
	
	@Test
	public void testGetKey() throws JAXBException, IOException {
		try (MockedStatic<AccountingLineUtil> theMock = Mockito.mockStatic(AccountingLineUtil.class)) {
			theMock.when(() -> AccountingLineUtil.getKey(acctLine)).thenReturn("KTJ_89P_TR9495_HT");
			String s = AccountingLineUtil.getKey(acctLine);
			assertTrue("KTJ_89P_TR9495_HT".equals(s));
		}
	}
	
	@Before
	public void init() throws FileNotFoundException, JAXBException {
		File xmlFile = new File("src/test/resources/AccountingLine.xml");
		inputStream = new FileInputStream(xmlFile);
		
		JAXBContext context = JAXBContext.newInstance(AccountingLineX.class);
		accountingLineX = (AccountingLineX) context.createUnmarshaller()
		      .unmarshal( inputStream );
		
		acctLine = new AccountingLineBuilder().builder(acctLineBuilder -> 
		acctLineBuilder.setAccountingLineX(accountingLineX)
	      ).build();
		
		Map<String, AccountingLine> values = new HashMap<>();
		values.put("ac_key", acctLine);
		
		wrapper = new AccountingLineResponseWrapper<>();
		wrapper.setData("");
		wrapper.setStatus(Constants.Message.SUCCESS.value);
		wrapper.setMessage(Constants.Message.SUCCESS_MSG.value);
	}
}
