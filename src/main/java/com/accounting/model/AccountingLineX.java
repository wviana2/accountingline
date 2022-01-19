package com.accounting.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.accounting.model.from.AccountingLineXYZ;

@XmlRootElement(name = "AccountingLine")
public class AccountingLineX extends AccountingLineXYZ
        implements Serializable {

	private static final long serialVersionUID = 4007849807823034319L;

}

