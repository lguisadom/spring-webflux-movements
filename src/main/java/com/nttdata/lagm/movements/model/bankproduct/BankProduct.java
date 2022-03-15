package com.nttdata.lagm.movements.model.bankproduct;

import com.nttdata.lagm.movements.model.customer.Customer;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class BankProduct {
	private String id;
	private String accountNumber;
	private String cci;
	private Customer customer;
	private String amount;
	private Boolean status = true;
}