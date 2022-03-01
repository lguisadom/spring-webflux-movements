package com.nttdata.lagm.movements.model.account;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "bankAccount")
public class BankAccount extends BankProduct {
	private Integer typeId; // 1: saving | 2: current | 3: fixed term
	private String maintenanceFee;
	private Integer maxLimitMonthlyMovements;
	private Integer dayAllowed;
}
