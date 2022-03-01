package com.nttdata.lagm.movements.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.lagm.movements.model.account.BankProduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class Credit extends BankProduct {
	private Double creditLimit;
	
}
