package com.nttdata.lagm.movements.service;

import com.nttdata.lagm.movements.dto.request.BankingMovementRequestDto;
import com.nttdata.lagm.movements.dto.request.MovementRequestBetweenDatesDto;
import com.nttdata.lagm.movements.dto.request.MovementRequestDto;
import com.nttdata.lagm.movements.dto.request.TransferRequestDto;
import com.nttdata.lagm.movements.dto.response.BakingMovementResponseDto;
import com.nttdata.lagm.movements.model.BankingMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface BankingMovementService {
	Mono<BankingMovement> create(BankingMovement bankingMovement);
	Mono<BakingMovementResponseDto> deposit(MovementRequestDto movementRequestDto);
	Mono<BakingMovementResponseDto> withdraw(MovementRequestDto movementRequestDto);
	Mono<BakingMovementResponseDto> pay(MovementRequestDto movementRequestDto);
	Mono<BakingMovementResponseDto> charge(MovementRequestDto movementRequestDto);
	Flux<BankingMovement> findAll();
	Mono<BankingMovement> findById(String id);
	Flux<BankingMovement> findAllAccountMovementsByAccountNumber(String accountNumber);
	Flux<BankingMovement> findAllCreditMovementsByAccountNumber(String accountNumber);
	Flux<BankingMovement> findAllMovementsByDni(String dni);
	Flux<BankingMovement> findMovementsInCurrentMonthByAccountNumber(String accountNumber);
	Flux<BakingMovementResponseDto> transfer(TransferRequestDto transferRequestDto);
	Flux<BankingMovement> findBetweenDates(MovementRequestBetweenDatesDto movementRequestBetweenDatesDto);
}
