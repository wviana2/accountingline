package com.accounting.builder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Consumer;

import com.accounting.constants.Constants;
import com.accounting.model.AccountingLineX;
import com.accounting.model.to.AccountingLine;
import com.accounting.model.to.AccountingLineStatus;
import com.accounting.model.to.MonetaryAmount;
import com.accounting.model.to.MonetaryPercentage;
import com.accounting.util.AccountingLineUtil;

import lombok.Setter;

@Setter
public class AccountingLineBuilder {

	private AccountingLineX accountingLineX;

	public AccountingLineBuilder builder(Consumer<AccountingLineBuilder> builderFunction) {
		builderFunction.accept(this);
		return this;
	}
	
	private Monetary ma;
	
	private QAmount totalTaxAmt;
	
	private QAmount totalTaxSrchg;
	
	private QPercent pct;
	
	public AccountingLine build() {
		
			ma = (amt) -> {
				String floatAmtStr = AccountingLineUtil.getStrAmt(amt);
				Integer noOfDecimals = AccountingLineUtil.getNoOfDecimals(floatAmtStr);
				return MonetaryAmount.builder()
						.value(Float.valueOf(floatAmtStr).longValue())
						.formattedValue(String.format("%,.".concat(noOfDecimals.toString()).concat("f"), 
								Double.parseDouble(floatAmtStr)))
						.numberOfDecimals(noOfDecimals)
						.currencyCode(Constants.USD)
						.build();
			};
			
			totalTaxAmt = () -> {
				String taxAmountStr = AccountingLineUtil.getStrAmt(accountingLineX.getTaxAmount());
				String qstAmtStr = AccountingLineUtil.getStrAmt(accountingLineX.getQSTAmount());
				String gstAmtStr = AccountingLineUtil.getStrAmt(accountingLineX.getGSTAmount());
				Float taxAmount = Float.parseFloat(taxAmountStr);
				Float qstAmount = Float.parseFloat(qstAmtStr);
				Float gstAmount = Float.parseFloat(gstAmtStr);
				Float totalTaxSurcharge = qstAmount + gstAmount;
				Float totalTaxAmount = taxAmount + totalTaxSurcharge;		
				String totalTaxAmtStr = totalTaxAmount.toString();
				return ma.getAmount(totalTaxAmtStr);
			};
			
			totalTaxSrchg = () -> {
				String qstAmtStr = AccountingLineUtil.getStrAmt(accountingLineX.getQSTAmount());
				String gstAmtStr = AccountingLineUtil.getStrAmt(accountingLineX.getGSTAmount());
				Float qstAmount = Float.parseFloat(qstAmtStr);
				Float gstAmount = Float.parseFloat(gstAmtStr);
				Float totalTaxSurcharge = qstAmount + gstAmount;
				String totalTaxSurchargeStr = totalTaxSurcharge.toString();		
				return ma.getAmount(totalTaxSurchargeStr);
			};
			
			pct = () -> {
				String commissionPctStr = AccountingLineUtil.getStrAmt(accountingLineX.getCommissionPercentage());		
				return MonetaryPercentage.builder()
						.amount(ma.getAmount(accountingLineX.getCommissionAmount()))
						.percentage(BigDecimal.valueOf(Double.parseDouble(commissionPctStr)))
						.build();
			};
		
		return AccountingLine.builder()
				.accountingLineID(accountingLineX.getId())
				.accountingLineStatus(AccountingLineStatus.ACTIVE)
				.accountingVendorCode(accountingLineX.getAccountingVendorCode())
				.airlineCode(null)
				.chargeCategoryCode(accountingLineX.getChargeCategoryCoded())
				.formattedReceiptNumber(null)
				.invoiceNumber(accountingLineX.getOriginalInvoice())
				.linkCode(accountingLineX.getLinkCode())
				.numberOfConjunctedDocuments(accountingLineX.getNumberOfConjunctedDocuments())
				.originalTicketNumber(accountingLineX.getOriginalTicketNumber())
				.receiptNumber(accountingLineX.getDocumentNumber())
				.segmentRefIDList(Arrays.asList(accountingLineX.getSegmentNumber()))
				.travelerName(accountingLineX.getPassengerName())
				.travelerRefIDList(Arrays.asList("1"))
				.typeIndicator(accountingLineX.getTypeIndicator())
				.elementNumber(accountingLineX.getIndex().toString())
				.fareApplication(accountingLineX.getFareApplication())
				.baseFare(ma.getAmount(accountingLineX.getBaseFare()))
				.taxAmount(ma.getAmount(accountingLineX.getTaxAmount()))
				.totalTaxAmount( totalTaxAmt.getAmount())
				.totalTaxSurcharge(totalTaxSrchg.getAmount())
				.gstAmount(ma.getAmount(accountingLineX.getQSTAmount()))
				.gstCode(accountingLineX.getGSTCode())
				.qstAmount(ma.getAmount(accountingLineX.getQSTAmount()))
				.qstCode(accountingLineX.getQSTCode())
				.commission(pct.getPercentage())
				.freeFormText(accountingLineX.getFreeFormText())
				.build();
	}
}
