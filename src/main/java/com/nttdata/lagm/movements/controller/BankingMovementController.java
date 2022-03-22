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

import com.nttdata.lagm.movements.dto.request.MovementRequestDto;
import com.nttdata.lagm.movements.dto.response.BakingMovementResponseDto;
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
	private Mono<BakingMovementResponseDto> deposit(@RequestBody MovementRequestDto movementRequestDto) {
		return bankingMovementService.deposit(movementRequestDto);
	}
	
	@PostMapping("/withdraw")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponseDto> withdraw(@RequestBody MovementRequestDto movementRequestDto) {
		return bankingMovementService.withdraw(movementRequestDto);
	}
	
	@PostMapping("/pay")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponseDto> pay(@RequestBody MovementRequestDto movementRequestDto) {
		return bankingMovementService.pay(movementRequestDto);
	}
	
	@PostMapping("/charge")
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<BakingMovementResponseDto> charge(@RequestBody MovementRequestDto movementRequestDto) {
		return bankingMovementService.charge(movementRequestDto);
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

	@GetMapping(value="/findMovementsInCurrentMonthByAccountNumber/{accountNumber}")
	@ResponseStatus(HttpStatus.OK)
	private Flux<BankingMovement> findMovementsInCurrentMonthByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		return bankingMovementService.findMovementsInCurrentMonthByAccountNumber(accountNumber);
	}

}
