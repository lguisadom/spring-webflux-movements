package com.nttdata.lagm.movements.entity;

import com.nttdata.lagm.movements.model.BankingMovement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BakingMovementResponse {
	private BankingMovement bankingMovement;
	private String message;
}
