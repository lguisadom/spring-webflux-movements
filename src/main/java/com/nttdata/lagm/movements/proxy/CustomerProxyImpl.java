package com.nttdata.lagm.movements.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.lagm.movements.model.customer.Customer;
import com.nttdata.lagm.movements.util.RestUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerProxyImpl implements CustomerProxy {
	
	@Value("${config-eureka.base.customer.endpoint}")
	private String endpointCustomer;
	
	@Autowired
	@Qualifier("wcLoadBalanced")
	private WebClient.Builder webClientBuilder;

	@Override
	public Flux<Customer> findAll() {
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCustomer)
				.retrieve()
				.bodyToFlux(Customer.class);
	}
	
	@Override
	public Mono<Customer> findById(String id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCustomer + "/{id}", params)
				.retrieve()
				.bodyToMono(Customer.class);
	}

	@Override
	public Mono<Customer> findByDni(String dni) {
		Map<String,Object> params = new HashMap<>();
		params.put("dni", dni);
		return webClientBuilder
				.clientConnector(RestUtils.getDefaultClientConnector())
				.build()
				.get()
				.uri(endpointCustomer + "/dni/{dni}", params)
				.retrieve()
				.bodyToMono(Customer.class);
	}
}
