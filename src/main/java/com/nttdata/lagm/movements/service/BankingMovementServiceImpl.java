package com.nttdata.lagm.movements.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.movements.model.BankingMovement;
import com.nttdata.lagm.movements.proxy.AccountProxy;
import com.nttdata.lagm.movements.repository.BankingMovementRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankingMovementServiceImpl implements BankingMovementService {

	private Logger LOGGER = LoggerFactory.getLogger(BankingMovementServiceImpl.class);
	
	@Autowired
	private BankingMovementRepository bankingMovementRepository;
	
	@Autowired
	private AccountProxy accountProxy;
	
	@Override
	public void create(BankingMovement bankingMovement) {
		bankingMovementRepository.save(bankingMovement).subscribe();
	}

	@Override
	public Flux<BankingMovement> findAll() {
		return bankingMovementRepository.findAll();
	}

	@Override
	public Mono<BankingMovement> findById(Long id) {
		return bankingMovementRepository.findById(id);
	}

}
