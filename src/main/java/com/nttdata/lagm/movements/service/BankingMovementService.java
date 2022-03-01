package com.nttdata.lagm.movements.service;

import com.nttdata.lagm.movements.model.BankingMovement;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BankingMovementService {
	void create(BankingMovement bankingMovement);
	Flux<BankingMovement> findAll();
	Mono<BankingMovement> findById(Long id);
}
