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
import com.nttdata.lagm.movements.proxy.CustomerProxy;
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
	
	@Autowired
	private CustomerProxy customerProxy;

	@Override
	public Mono<BankingMovement> create(BankingMovement bankingMovement) {
		LOGGER.info("Create movement: " + bankingMovement);
		return checkBankProductExist(bankingMovement.getBankProductId(), bankingMovement.getBankingMovementType())
				.mergeWith(checkBankMovementTypeExist(bankingMovement.getBankingMovementType()))
				.then(this.bankingMovementRepository.save(bankingMovement));
	}
	
	@Override
	public Mono<BakingMovementResponse> deposit(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return checkAccountNumberExists(accountNumber)
				.then(accountProxy.findByAccountNumber(accountNumber).flatMap(account -> {
					LOGGER.info("account: " + account.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(account.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.toString());

					return create(bankingMovement).flatMap(movement -> {
						return accountProxy.updateAmount(movement.getBankProductId(), movement.getAmount())
								.flatMap(accountUpdated -> {
									BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
											"Movement ok");
									return Mono.just(bakingMovementResponse);
								});
					});
				}));
	}

	@Override
	public Mono<BakingMovementResponse> withdraw(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return checkAccountNumberExists(accountNumber)
				.mergeWith(checkAvailableAmountInAccount(movementRequest))
				.then(accountProxy.findByAccountNumber(accountNumber).flatMap(account -> {
					LOGGER.info("account: " + account.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(account.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.multiply(new BigDecimal(-1)).toString());

					return create(bankingMovement).flatMap(movement -> {
						return accountProxy.updateAmount(movement.getBankProductId(), movement.getAmount())
								.flatMap(accountUpdated -> {
									BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
											"Movement ok");
									return Mono.just(bakingMovementResponse);
								});
					});
				}));
	}

	@Override
	public Mono<BakingMovementResponse> pay(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return checkCreditNumberExists(accountNumber)
				.then(creditProxy.findByAccountNumber(accountNumber).flatMap(credit -> {
					LOGGER.info("credit: " + credit.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.toString());

					return create(bankingMovement).flatMap(movement -> {
						return creditProxy
								.updateAmount(movement.getBankProductId(), Double.valueOf(movement.getAmount()))
								.flatMap(accountUpdated -> {
									BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
											"Movement ok");
									return Mono.just(bakingMovementResponse);
								});
					});
				}));
	}

	@Override
	public Mono<BakingMovementResponse> charge(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return checkCreditNumberExists(accountNumber)
				.mergeWith(checkAvailableAmountInCredit(movementRequest))
				.then(creditProxy.findByAccountNumber(accountNumber).flatMap(credit -> {
					LOGGER.info("credit: " + credit.toString());
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_CHARGE);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount.multiply(new BigDecimal(-1)).toString());

					return create(bankingMovement).flatMap(movement -> {

						return creditProxy
								.updateAmount(movement.getBankProductId(), Double.valueOf(movement.getAmount()))
								.flatMap(accountUpdated -> {
									BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
											"Movement ok");
									return Mono.just(bakingMovementResponse);
								});
					});
				}));
	}


	@Override
	public Flux<BankingMovement> findAll() {
		return bankingMovementRepository.findAll();
	}

	@Override
	public Mono<BankingMovement> findById(String id) {
		return bankingMovementRepository.findById(id);
	}

	private Mono<Void> checkBankProductExist(String bankProductId, Integer bankingMovementTypeId) {

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
	
	private Mono<Void> checkAvailableAmountInAccount(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return accountProxy.findByAccountNumber(accountNumber)
				.flatMap(account -> {
					BigDecimal availableAmount = new BigDecimal(account.getAmount());
					if (amount.compareTo(availableAmount) > 0) {
						return Mono.error(new Exception("El monto a retirar es mayor al disponible"));
					}
					
					return Mono.empty();
				});
	}
	
	private Mono<Void> checkAvailableAmountInCredit(MovementRequest movementRequest) {
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return creditProxy.findByAccountNumber(accountNumber)
				.flatMap(credit -> {
					BigDecimal currentAmount = new BigDecimal(credit.getAmount());
					BigDecimal creditLimit = new BigDecimal(credit.getCreditLimit());
					BigDecimal availableAmount = creditLimit.add(currentAmount);
					if (amount.compareTo(availableAmount) > 0) {
						return Mono.error(new Exception("El monto a consumir es mayor que el disponible"));
					}
					
					return Mono.empty();
				});
	}
	
	private Mono<Void> checkAccountNumberExists(String accountNumber) {
		return accountProxy.findByAccountNumber(accountNumber)
				.switchIfEmpty(Mono
						.error(new Exception("Cuenta bancaria con número de cuenta: " + accountNumber + " no existe")))
				.then();
	}
	
	private Mono<Void> checkCreditNumberExists(String accountNumber) {
		return creditProxy.findByAccountNumber(accountNumber)
				.switchIfEmpty(
						Mono.error(new Exception("Crédito con número de cuenta: " + accountNumber + " no existe")))
				.then();
	}

	@Override
	public Flux<BankingMovement> findAllAccountMovementsByAccountNumber(String accountNumber) {
		return accountProxy.findByAccountNumber(accountNumber).flatMapMany(account -> {
			return bankingMovementRepository.findAll()
					.filter(movement -> movement.getBankProductId().equals(account.getId()));
		});
	}

	@Override
	public Flux<BankingMovement> findAllCreditMovementsByAccountNumber(String accountNumber) {
		return creditProxy.findByAccountNumber(accountNumber)
			.flatMapMany(credit -> {
				return bankingMovementRepository.findAll()
					.filter(movement -> movement.getBankProductId().equals(credit.getId()));
			});
	}

	@Override
	public Flux<BankingMovement> findAllMovementsByDni(String dni) {
		return customerProxy.findByDni(dni)
			.flatMapMany(customer -> {
				return accountProxy.findAll()
						.filter(account -> account.getCustomerId().equals(customer.getId()));
			})
			.flatMap(account -> {
				return bankingMovementRepository.findAll()
						.filter(movement -> movement.getBankProductId().equals(account.getId()));
			});
	}
}
