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
	Mono<BankingMovement> pay(MovementRequest movementRequest);
	Mono<BankingMovement> charge(MovementRequest movementRequest);
	Flux<BankingMovement> findAll();
	Mono<BankingMovement> findById(String id);
}
