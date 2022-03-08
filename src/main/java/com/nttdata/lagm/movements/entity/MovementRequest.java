package com.nttdata.lagm.movements.entity;

import lombok.Data;

@Data
public class MovementRequest {
	public String accountNumber;
	public String amount;
}
