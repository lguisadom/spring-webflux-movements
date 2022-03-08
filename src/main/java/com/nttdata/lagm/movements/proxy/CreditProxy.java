package com.nttdata.lagm.movements.proxy;

import com.nttdata.lagm.movements.model.bankproduct.Credit;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditProxy {
	public Flux<Credit> findAll();
	public Mono<Credit> findById(Long id);
	public Mono<Credit> update(Credit credit);
}
