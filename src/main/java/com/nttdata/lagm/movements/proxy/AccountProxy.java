package com.nttdata.lagm.movements.proxy;

import com.nttdata.lagm.movements.model.account.BankAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountProxy {
	public Flux<BankAccount> findAll();
	public Mono<BankAccount> findById(Long id);
	public Mono<BankAccount> update(BankAccount bankAccount);
}
