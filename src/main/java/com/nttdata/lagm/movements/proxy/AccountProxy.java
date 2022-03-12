package com.nttdata.lagm.movements.proxy;

import com.nttdata.lagm.movements.model.bankproduct.BankAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountProxy {
	public Flux<BankAccount> findAll();
	public Mono<BankAccount> findById(String id);
	public Mono<BankAccount> findByAccountNumber(String accountNumber);
	public Mono<BankAccount> update(BankAccount bankAccount);
	public Mono<BankAccount> updateAmount(String id, String amount);
}
