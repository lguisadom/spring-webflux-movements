package com.nttdata.lagm.movements.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
	@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime date;
	private String accountNumber;
	private String bankingFee;
	private BigDecimal amount;
	private BigDecimal commision;
	private BigDecimal finalAmount;
}
