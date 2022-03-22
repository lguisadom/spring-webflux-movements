package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

@Data
public class TransferRequestDto {
	private String sourceAccount;
	private String targetAccount;
	private String amount;
}
