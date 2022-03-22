package com.nttdata.lagm.movements.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementRequestBetweenDatesDto {
    private String from;
    private String to;
}
