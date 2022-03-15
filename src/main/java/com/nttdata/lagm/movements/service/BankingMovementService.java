package com.nttdata.lagm.movements.service;

import com.nttdata.lagm.movements.entity.BakingMovementResponse;
import com.nttdata.lagm.movements.entity.MovementRequest;
import com.nttdata.lagm.movements.model.BankingMovement;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankingMovementService {
	Mono<BankingMovement> create(BankingMovement bankingMovement);
	Mono<BakingMovementResponse> deposit(MovementRequest movementRequest);
	Mono<BakingMovementResponse> withdraw(MovementRequest movementRequest);
	Mono<BakingMovementResponse> pay(MovementRequest movementRequest);
	Mono<BakingMovementResponse> charge(MovementRequest movementRequest);
	Flux<BankingMovement> findAll();
	Mono<BankingMovement> findById(String id);
	Flux<BankingMovement> findAllAccountMovementsByAccountNumber(String accountNumber);
	Flux<BankingMovement> findAllCreditMovementsByAccountNumber(String accountNumber);
	Flux<BankingMovement> findAllMovementsByDni(String dni);
	Flux<BankingMovement> findMovementsInCurrentMonthByAccountNumber(String accountNumber);
}
