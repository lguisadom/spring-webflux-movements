package com.nttdata.lagm.movements.dto.response;

import com.nttdata.lagm.movements.model.BankingMovement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BakingMovementResponseDto {
	private BankingMovement bankingMovement;
	private String message;
}
