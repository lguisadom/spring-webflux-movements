package com.nttdata.lagm.movements.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.nttdata.lagm.movements.model.customer.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CustomerProxyImpl implements CustomerProxy {
	
	@Value("${config.base.customer.endpoint}")
	private String endpointCustomer;
	
	private WebClient webClient = WebClient.create();

	@Override
	public Flux<Customer> findAll() {
		return webClient.get().uri(endpointCustomer)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(Customer.class);
	}
	
	@Override
	public Mono<Customer> findById(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		
		Mono<Customer> customerMono = webClient.get().uri(endpointCustomer + "/{id}", params)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(Customer.class);
		
		customerMono.subscribe(System.out::print);
		return customerMono;
		
	}

}
