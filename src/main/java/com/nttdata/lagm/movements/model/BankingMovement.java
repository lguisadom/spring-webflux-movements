package com.nttdata.lagm.movements.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "bankingMovement")
@ToString
public class BankingMovement {
	@Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
	private String bankProductId;
	// private Long bankProductTypeId;
	private BankingMovementType bankingMovementType;
	private String date;
	private String bankingFee;
	private BigDecimal amount;
	private BigDecimal commision;
	private BigDecimal finalAmount;
}
