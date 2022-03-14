package com.nttdata.lagm.movements.proxy;

import com.nttdata.lagm.movements.model.customer.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerProxy {
	public Flux<Customer> findAll();
	public Mono<Customer> findById(String id);
	public Mono<Customer> findByDni(String dni);
}
