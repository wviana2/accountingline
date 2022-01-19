package com.accounting.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AccountingLineResponseWrapper<T> implements Serializable {

	private static final long serialVersionUID = 4690444525369115965L;

	private String status;
	
	private String message;
	
	private T data;

}
