package com.nttdata.lagm.movements.proxy;

import com.nttdata.lagm.movements.model.bankproduct.Credit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditProxy {
	public Flux<Credit> findAll();
	public Mono<Credit> findById(String id);
	public Mono<Credit> findByAccountNumber(String accountNumber);
	public Mono<Credit> update(Credit credit);
	public Mono<Credit> updateAmount(String id, Double amount);
}
