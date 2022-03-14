package com.nttdata.lagm.movements.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.lagm.movements.entity.BakingMovementResponse;
import com.nttdata.lagm.movements.entity.MovementRequest;
import com.nttdata.lagm.movements.model.BankingMovement;
import com.nttdata.lagm.movements.service.BankingMovementService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/movement")
public class BankingMovementController {

	@Autowired
	private BankingMovementService bankingMovementService;
	
	@PostMapping("/deposit")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponse> deposit(@RequestBody MovementRequest movementRequest) {
		return bankingMovementService.deposit(movementRequest);
	}
	
	@PostMapping("/withdraw")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponse> withdraw(@RequestBody MovementRequest movementRequest) {
		return bankingMovementService.withdraw(movementRequest);
	}
	
	@PostMapping("/pay")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponse> pay(@RequestBody MovementRequest movementRequest) {
		return bankingMovementService.pay(movementRequest);
	}
	
	@PostMapping("/charge")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponse> charge(@RequestBody MovementRequest movementRequest) {
		return bankingMovementService.charge(movementRequest);
	}
	
	@GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankingMovement>findAll() {
		return bankingMovementService.findAll();
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	private Mono<BankingMovement>findById(@PathVariable("id") String id) {
		return bankingMovementService.findById(id);
	}
	
	@GetMapping(value = "/bankAccount/accountNumber/{accountNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankingMovement> findAllAccountMovementsByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		return bankingMovementService.findAllAccountMovementsByAccountNumber(accountNumber);
	}
	
	@GetMapping(value = "/credit/accountNumber/{accountNumber}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankingMovement> findAllCreditMovementsByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		return bankingMovementService.findAllCreditMovementsByAccountNumber(accountNumber);
	}

	@GetMapping(value="/dni/{dni}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankingMovement> findAllMovementsByDni(@PathVariable("dni") String dni) {
		return bankingMovementService.findAllMovementsByDni(dni);
	}
}
