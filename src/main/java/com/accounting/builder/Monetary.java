package com.accounting.builder;

import com.accounting.model.to.MonetaryAmount;

public interface Monetary {

	MonetaryAmount getAmount(String amt);
}
