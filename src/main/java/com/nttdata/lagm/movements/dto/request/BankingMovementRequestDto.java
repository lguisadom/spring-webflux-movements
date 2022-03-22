package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankingMovementRequestDto {
    private String id;
    private String bankProductId;
    private Integer bankingMovementType;
    private String date;
    private String bankingFee;
    private BigDecimal amount;
    private BigDecimal commision;
    private BigDecimal finalAmount;
}
