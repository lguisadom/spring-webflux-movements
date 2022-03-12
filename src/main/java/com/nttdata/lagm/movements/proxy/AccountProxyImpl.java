package com.nttdata.lagm.movements.proxy;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.lagm.movements.model.bankproduct.BankAccount;
import com.nttdata.lagm.movements.util.RestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AccountProxyImpl implements AccountProxy {
	
	private Logger LOGGER = LoggerFactory.getLogger(AccountProxyImpl.class);
	
	@Value("${config-eureka.base.account.endpoint}")
	private String endpointAccount;
	
	@Autowired
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;
	
	private WebClient webClient = WebClient.create();

	@Override
	public Flux<BankAccount> findAll() {
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointAccount)
				.retrieve()
				.bodyToFlux(BankAccount.class);
	}
	
	@Override
	public Mono<BankAccount> findById(String id) {		
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointAccount + "/{id}", params)
				.retrieve()
				.bodyToMono(BankAccount.class);
		
	}
	
	@Override
	public Mono<BankAccount> findByAccountNumber(String accountNumber) {		
		Map<String,Object> params = new HashMap<>();
		params.put("accountNumber", accountNumber);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointAccount + "/accountNumber/{accountNumber}", params)
				.retrieve()
				.bodyToMono(BankAccount.class);
		
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
	
	@Override
	public Mono<BankAccount> updateAmount(String id, String amount) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("amount", amount);
		String url = endpointAccount + "/update/{id}/amount/{amount}";
		LOGGER.info("url: " + url);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.put()
				.uri(url, params)
				.retrieve()
				.bodyToMono(BankAccount.class);
	}
}
