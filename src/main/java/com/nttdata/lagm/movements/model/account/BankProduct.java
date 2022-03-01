package com.nttdata.lagm.movements.model.account;

import lombok.Data;

@Data
public abstract class BankProduct {
	private Integer id;
	private String accountNumber;
	private String cci;
	private String amount;
	private Long customerId;	
}