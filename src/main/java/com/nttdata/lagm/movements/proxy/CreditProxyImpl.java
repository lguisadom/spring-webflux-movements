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

import com.nttdata.lagm.movements.model.bankproduct.Credit;
import com.nttdata.lagm.movements.util.RestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CreditProxyImpl implements CreditProxy {
	private Logger LOGGER = LoggerFactory.getLogger(CreditProxyImpl.class);

	@Value("${config-eureka.base.credit.endpoint}")
	private String endpointCredit;

	@Autowired
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;

	@Override
	public Flux<Credit> findAll() {
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCredit)
				.retrieve()
				.bodyToFlux(Credit.class);
	}

	@Override
	public Mono<Credit> findById(String id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCredit + "/{id}", params)
				.retrieve()
				.bodyToMono(Credit.class);
	}
	
	@Override
	public Mono<Credit> findByAccountNumber(String accountNumber) {		
		Map<String,Object> params = new HashMap<>();
		params.put("accountNumber", accountNumber);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCredit + "/accountNumber/{accountNumber}", params)
				.retrieve()
				.bodyToMono(Credit.class);
		
	}

	@Override
	public Mono<Credit> update(Credit credit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Credit> updateAmount(String id, Double amount) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("amount", amount);
		String url = endpointCredit + "/update/{id}/amount/{amount}";
		LOGGER.info("url: " + url);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.put()
				.uri(url, params)
				.retrieve()
				.bodyToMono(Credit.class);
	}	
}
