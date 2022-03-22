package com.nttdata.lagm.movements.model.bankproduct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BankAccountType {
    private Integer id; // 1: saving | 2: current | 3: fixed term
    private String description;
    private String commision;
}
