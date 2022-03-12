package com.nttdata.lagm.movements.model.bankproduct;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class BankProduct {
	private String id;
	private String accountNumber;
	private String cci;
	private Long customerId;
	private String amount;
	private Boolean status;
}