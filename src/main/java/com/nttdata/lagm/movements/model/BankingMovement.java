package com.nttdata.lagm.movements.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bankingMovement")
public class BankingMovement {
	private Long id;
	private Long bankProductId;
	// private Long bankProductTypeId;
	private Integer bankingMovementType;
	private String date;
	private String amount;
}
