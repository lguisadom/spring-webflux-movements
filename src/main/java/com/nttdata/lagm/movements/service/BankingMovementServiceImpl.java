package com.nttdata.lagm.movements.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.movements.entity.BakingMovementResponse;
import com.nttdata.lagm.movements.entity.MovementRequest;
import com.nttdata.lagm.movements.model.BankingMovement;
import com.nttdata.lagm.movements.proxy.AccountProxy;
import com.nttdata.lagm.movements.proxy.CreditProxy;
import com.nttdata.lagm.movements.repository.BankingMovementRepository;
import com.nttdata.lagm.movements.util.Constants;
import com.nttdata.lagm.movements.util.Util;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BankingMovementServiceImpl implements BankingMovementService {

	private Logger LOGGER = LoggerFactory.getLogger(BankingMovementServiceImpl.class);

	@Autowired
	private BankingMovementRepository bankingMovementRepository;

	@Autowired
	private AccountProxy accountProxy;
	
	@Autowired
	private CreditProxy creditProxy;

	@Override
	public Mono<BankingMovement> create(BankingMovement bankingMovement) {
		LOGGER.info("Create movement: " + bankingMovement);
		return //checkMovementNotExists(bankingMovement.getId())
				checkBankProductExist(bankingMovement.getBankProductId(), bankingMovement.getBankingMovementType())
				.mergeWith(checkBankMovementTypeExist(bankingMovement.getBankingMovementType()))
				.then(this.bankingMovementRepository.save(bankingMovement));
	}
	
	@Override
	public Mono<BakingMovementResponse> deposit(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return accountProxy.findByAccountNumber(accountNumber)
				.flatMap(account -> {
					LOGGER.info("account: " + account.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(account.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.toString());

					return create(bankingMovement)
							.flatMap(movement -> {
								return accountProxy.updateAmount(movement.getBankProductId(), movement.getAmount())
										.flatMap(accountUpdated -> {
											BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement, "Movement ok");
											return Mono.just(bakingMovementResponse);
										});
							});
				});
	}

	@Override
	public Mono<BakingMovementResponse> withdraw(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return accountProxy.findByAccountNumber(accountNumber)
				.flatMap(account -> {
					LOGGER.info("account: " + account.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(account.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.multiply(new BigDecimal(-1)).toString());

					return create(bankingMovement)
							.flatMap(movement -> {
								return accountProxy.updateAmount(movement.getBankProductId(), movement.getAmount())
										.flatMap(accountUpdated -> {
											BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement, "Movement ok");
											return Mono.just(bakingMovementResponse);
										});
							});
				});
	}

	@Override
	public Mono<BakingMovementResponse> pay(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return creditProxy.findByAccountNumber(accountNumber)
				.flatMap(credit -> {
					LOGGER.info("credit: " + credit.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.toString());

					return create(bankingMovement)
							.flatMap(movement -> {
								return creditProxy.updateAmount(movement.getBankProductId(), Double.valueOf(movement.getAmount()))
										.flatMap(accountUpdated -> {
											BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement, "Movement ok");
											return Mono.just(bakingMovementResponse);
										});
							});
				});
	}

	@Override
	public Mono<BakingMovementResponse> charge(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return creditProxy.findByAccountNumber(accountNumber)
				.flatMap(credit -> {
					LOGGER.info("credit: " + credit.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_CHARGE);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.multiply(new BigDecimal(-1)).toString());

					return create(bankingMovement)
							.flatMap(movement -> {
								
								return creditProxy.updateAmount(movement.getBankProductId(), Double.valueOf(movement.getAmount()))
										.flatMap(accountUpdated -> {
											BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement, "Movement ok");
											return Mono.just(bakingMovementResponse);
										});
							});
				});
	}

	@Override
	public Flux<BankingMovement> findAll() {
		return bankingMovementRepository.findAll();
	}

	@Override
	public Mono<BankingMovement> findById(String id) {
		return bankingMovementRepository.findById(id);
	}

	private Mono<Void> checkBankProductExist(Long bankProductId, Integer bankingMovementTypeId) {

		if (Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT == bankingMovementTypeId ||
			Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL == bankingMovementTypeId) {
			return accountProxy.findById(bankProductId)
					.switchIfEmpty(Mono.error(new Exception("No existe cuenta bancaria con id: " + bankProductId)))
					.then();
			
		} else if (Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT == bankingMovementTypeId ||
				   Constants.ID_BANK_PRODUCT_CREDIT_CHARGE == bankingMovementTypeId) {
			return creditProxy.findById(bankProductId)
					.switchIfEmpty(Mono.error(new Exception("No existe crédito con id: " + bankProductId)))
					.then();
		}
		
		return Mono.empty();
	}
	
	private Mono<Void> checkBankMovementTypeExist(Integer bankingMovementTypeId) {
		if (Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT != bankingMovementTypeId && 
			Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL != bankingMovementTypeId && 
			Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT != bankingMovementTypeId &&
			Constants.ID_BANK_PRODUCT_CREDIT_CHARGE != bankingMovementTypeId) {
				return Mono.error(new Exception("Debe ingresar un id de movimiento bancario válido. " + Util.bankMovementTypeValidInfo()));
		}
		return Mono.empty();
	}
}
