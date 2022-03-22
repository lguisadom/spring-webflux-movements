package com.nttdata.lagm.movements.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovementRequestBetweenDatesDto {
    private String from;
    private String to;
}
