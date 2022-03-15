package com.nttdata.lagm.movements.model.bankproduct;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
