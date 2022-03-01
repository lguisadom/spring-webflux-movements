package com.nttdata.lagm.movements.proxy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.lagm.movements.model.account.BankAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AccountProxyImpl implements AccountProxy {
	
	private Logger LOGGER = LoggerFactory.getLogger(AccountProxyImpl.class);
	
	@Value("${config.base.account.endpoint}")
	private String endpointAccount;
	
	private WebClient webClient = WebClient.create();

	@Override
	public Flux<BankAccount> findAll() {
		return webClient.get().uri(endpointAccount)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(BankAccount.class);
	}
	
	@Override
	public Mono<BankAccount> findById(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		
		Mono<BankAccount> customerAccount = webClient.get().uri(endpointAccount + "/{id}", params)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(BankAccount.class);
		
		customerAccount.subscribe(System.out::print);
		return customerAccount;
		
	}

	@Override
	public Mono<BankAccount> update(BankAccount bankAccount) {
		LOGGER.info("update endpointAccount: " + endpointAccount);
		return webClient.put()
				.uri(endpointAccount)
				.body(Mono.just(bankAccount), BankAccount.class)
				// .accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(BankAccount.class);
	}

}
