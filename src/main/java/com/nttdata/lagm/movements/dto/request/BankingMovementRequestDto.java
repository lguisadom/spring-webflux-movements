package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankingMovementRequestDto {
    private String id;
    private String bankProductId;
    private Integer bankingMovementType;
    private LocalDateTime date;
    private String bankingFee;
    private BigDecimal amount;
    private BigDecimal commision;
    private BigDecimal finalAmount;
}
