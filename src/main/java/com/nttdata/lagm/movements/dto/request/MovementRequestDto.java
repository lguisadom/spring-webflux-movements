package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

@Data
public class MovementRequestDto {
	private String accountNumber;
	private String amount;
}
