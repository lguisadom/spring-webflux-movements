package com.nttdata.lagm.movements.util;

import com.nttdata.lagm.movements.dto.request.BankingMovementRequestDto;
import com.nttdata.lagm.movements.model.BankingMovement;
import com.nttdata.lagm.movements.model.BankingMovementType;

public class Converter {
    public static BankingMovement convertToBankingMovement(BankingMovementRequestDto bankingMovementRequestDto) {
        BankingMovement bankingMovement = new BankingMovement();
        bankingMovement.setBankProductId(bankingMovementRequestDto.getBankProductId());
        BankingMovementType bankingMovementType = new BankingMovementType();
        bankingMovementType.setId(bankingMovementRequestDto.getBankingMovementType());
        bankingMovementType.setDescription(Util.bankingMovementTypeDescription(bankingMovementRequestDto.getBankingMovementType()));
        bankingMovement.setBankingMovementType(bankingMovementType);
        bankingMovement.setDate(bankingMovementRequestDto.getDate());
        bankingMovement.setBankingFee(bankingMovementRequestDto.getBankingFee());
        bankingMovement.setAmount(bankingMovementRequestDto.getAmount());
        bankingMovement.setCommision(bankingMovementRequestDto.getCommision());
        bankingMovement.setFinalAmount(bankingMovementRequestDto.getFinalAmount());
        return bankingMovement;
    }
}
