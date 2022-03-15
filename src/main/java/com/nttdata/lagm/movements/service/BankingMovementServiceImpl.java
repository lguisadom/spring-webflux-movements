package com.nttdata.lagm.movements.service;

import java.math.BigDecimal;

import com.nttdata.lagm.movements.model.bankproduct.BankAccount;
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
		.then(accountProxy.findByAccountNumber(accountNumber)
			.flatMap(account -> {
				return numberMovementsInCurrentMonthByAccountNumber(accountNumber)
					.flatMap(numberMovements -> {
						BigDecimal availableAmount = new BigDecimal(account.getAmount());
						LOGGER.info("Available amount in account: " + availableAmount);
						BigDecimal commision = this.calculateCommision(numberMovements, account);
						LOGGER.info("NumberMovements: " + numberMovements);
						LOGGER.info("MaxMovements: " + account.getMaxLimitMonthlyMovements());
						LOGGER.info("Deposit Commision: " + commision);

						BigDecimal aditional = commision.multiply(amount);
						BigDecimal finalAmount = amount.subtract(aditional);
						LOGGER.info("Initial amount to withdraw: " + amount);
						LOGGER.info("Aditional (commision): " + aditional);
						LOGGER.info("Final Amount: " + finalAmount);

						// Validation of maximum number of transactions
						BankingMovement bankingMovement = new BankingMovement();
						bankingMovement.setBankProductId(account.getId());
						bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT);
						bankingMovement.setDate(Util.getToday());
						bankingMovement.setAmount(amount);
						bankingMovement.setCommision(commision);
						bankingMovement.setFinalAmount(finalAmount);

						return create(bankingMovement).flatMap(movement -> {
							LOGGER.info("Remaining amount in account: " + availableAmount.subtract(finalAmount));
							return accountProxy.updateAmount(movement.getBankProductId(), finalAmount.toString())
								.flatMap(accountUpdated -> {
									BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
											"Movement ok");
									return Mono.just(bakingMovementResponse);
								});
						});
					});
			}));
	}

	@Override
	public Mono<BakingMovementResponse> withdraw(MovementRequest movementRequest) {
		LOGGER.info("Movements withdraw: ");
		BigDecimal amount = new BigDecimal(movementRequest.getAmount());
		String accountNumber = movementRequest.getAccountNumber();
		return checkAccountNumberExists(accountNumber)
			.then(accountProxy.findByAccountNumber(accountNumber)
				.flatMap(account -> {
					return numberMovementsInCurrentMonthByAccountNumber(accountNumber)
						.flatMap(numberMovements -> {
							BigDecimal availableAmount = new BigDecimal(account.getAmount());
							LOGGER.info("Available amount in account: " + availableAmount);
							BigDecimal commision = this.calculateCommision(numberMovements, account);
							LOGGER.info("NumberMovements: " + numberMovements);
							LOGGER.info("MaxMovements: " + account.getMaxLimitMonthlyMovements());
							LOGGER.info("Deposit Commision: " + commision);

							BigDecimal aditional = commision.multiply(amount);
							BigDecimal finalAmount = amount.add(aditional);
							LOGGER.info("Initial amount to withdraw: " + amount);
							LOGGER.info("Aditional (commision): " + aditional);
							LOGGER.info("Final Amount: " + finalAmount);

							// Validation of maximum number of transactions
							BankingMovement bankingMovement = new BankingMovement();
							bankingMovement.setBankProductId(account.getId());
							bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL);
							bankingMovement.setDate(Util.getToday());
							bankingMovement.setAmount(amount);
							bankingMovement.setCommision(commision);
							bankingMovement.setFinalAmount(finalAmount);

							return checkAvailableAmountInAccount(availableAmount, finalAmount)
								.then(create(bankingMovement).flatMap(movement -> {
									LOGGER.info("Remaining amount in account: " + availableAmount.subtract(finalAmount));
									return accountProxy.updateAmount(movement.getBankProductId(), finalAmount.multiply(new BigDecimal(-1)).toString())
									.flatMap(accountUpdated -> {
										BakingMovementResponse bakingMovementResponse = new BakingMovementResponse(movement,
												"Movement ok");
										return Mono.just(bakingMovementResponse);
									});
							}));
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
					BigDecimal finalAmount = amount;
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount);
					bankingMovement.setFinalAmount(finalAmount);

					return create(bankingMovement).flatMap(movement -> {
						return creditProxy
								.updateAmount(movement.getBankProductId(), movement.getAmount().doubleValue())
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
					BigDecimal finalAmount = amount;
					BankingMovement bankingMovement = new BankingMovement();
					bankingMovement.setBankProductId(credit.getId());
					bankingMovement.setBankingMovementType(Constants.ID_BANK_PRODUCT_CREDIT_CHARGE);
					bankingMovement.setDate(Util.getToday());
					bankingMovement.setAmount(amount);
					bankingMovement.setFinalAmount(finalAmount);

					return create(bankingMovement).flatMap(movement -> {
						return creditProxy
								.updateAmount(movement.getBankProductId(), movement.getAmount().doubleValue() * -1)
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

	private Mono<Void> checkAvailableAmountInAccount(BigDecimal availableAmount, BigDecimal finalAmount) {
		if (finalAmount.compareTo(availableAmount) > 0) {
			return Mono.error(new Exception("El monto a retirar es mayor al disponible"));
		}

		return Mono.empty();
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
							.filter(account -> account.getCustomer().getId().equals(customer.getId()));
				})
				.flatMap(account -> {
					return bankingMovementRepository.findAll()
							.filter(movement -> movement.getBankProductId().equals(account.getId()));
				});
	}

	public Flux<BankingMovement> findMovementsInCurrentMonthByAccountNumber(String accountNumber) {
		int currentMonth = Util.getCurrentMonth();
		int currentYear = Util.getCurrentYear();
		return accountProxy.findByAccountNumber(accountNumber)
				.flatMapMany(bankAccount -> {
					String bankAccountId = bankAccount.getId();
					return bankingMovementRepository.findAll()
							.filter(movement ->
									movement.getBankProductId().equals(bankAccountId) &&
											Util.getMonthFromStringDate(movement.getDate()) == currentMonth &&
											Util.getYearFromStringDate(movement.getDate()) == currentYear);
				});
	}

	private Mono<Long> numberMovementsInCurrentMonthByAccountNumber(String accountNumber) {
		return findMovementsInCurrentMonthByAccountNumber(accountNumber).count();
	}

	private BigDecimal calculateCommision(Long numberMovements, BankAccount bankAccount) {
		Integer maxLimithMonthlyMovements = bankAccount.getMaxLimitMonthlyMovements();
		Integer bankAccountTypeId = bankAccount.getBankAccountType().getId();
		BigDecimal commision = new BigDecimal(0);

		if (bankAccountTypeId == Constants.ID_BANK_ACCOUNT_SAVING && numberMovements >= maxLimithMonthlyMovements) {
			commision = new BigDecimal(bankAccount.getBankAccountType().getCommision());
		}
		return commision;
	}
}
