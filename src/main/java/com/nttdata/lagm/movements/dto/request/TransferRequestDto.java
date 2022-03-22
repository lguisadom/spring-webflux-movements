package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

@Data
public class TransferRequestDto {
	private String sourceAccountNumber;
	private String targetAccountNumber;
	private String amount;
}
