package com.nttdata.lagm.movements.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.lagm.movements.dto.request.MovementRequestBetweenDatesDto;
import com.nttdata.lagm.movements.dto.request.MovementRequestDto;
import com.nttdata.lagm.movements.dto.request.TransferRequestDto;
import com.nttdata.lagm.movements.dto.response.BakingMovementResponseDto;
import com.nttdata.lagm.movements.model.BankingMovement;
import com.nttdata.lagm.movements.model.BankingMovementType;
import com.nttdata.lagm.movements.model.bankproduct.BankAccount;
import com.nttdata.lagm.movements.model.bankproduct.Credit;
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
		return checkBankProductExist(bankingMovement.getBankProductId(), bankingMovement.getBankingMovementType().getId())
				.mergeWith(checkBankMovementTypeExist(bankingMovement.getBankingMovementType().getId()))
				.then(this.bankingMovementRepository.save(bankingMovement));
	}

	@Override
	public Mono<BakingMovementResponseDto> deposit(MovementRequestDto movementRequestDto) {
		BigDecimal amount = new BigDecimal(movementRequestDto.getAmount());
		String accountNumber = movementRequestDto.getAccountNumber();

		return checkAccountNumberExists(accountNumber)
				.then(accountProxy.findByAccountNumber(accountNumber)
					.flatMap(account -> {
						return numberMovementsInCurrentMonthByAccountNumber(accountNumber)
							.flatMap(numberMovements -> {
								BigDecimal availableAmount = new BigDecimal(account.getAmount());
								LOGGER.info("Available amount in account accountNumber {}: {}", accountNumber, availableAmount);
								BigDecimal commision = this.calculateCommision(numberMovements, account);
								LOGGER.info("NumberMovements: " + numberMovements);
								LOGGER.info("MaxMovements: " + account.getMaxLimitMonthlyMovements());
								LOGGER.info("Deposit Commision: " + commision);

								BigDecimal aditional = commision.multiply(amount);
								BigDecimal finalAmount = amount.subtract(aditional);
								LOGGER.info("Initial amount to deposit: " + amount);
								LOGGER.info("Aditional (commision): " + aditional);
								LOGGER.info("Final Amount: " + finalAmount);

								// Validation of maximum number of transactions
								BankingMovement bankingMovement = new BankingMovement();
								bankingMovement.setBankProductId(account.getId());
								BankingMovementType bankingMovementType = new BankingMovementType();
								bankingMovementType.setId(Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT);
								String movementDescription = Util.bankingMovementTypeDescription(Constants.ID_BANK_PRODUCT_ACCOUNT_DEPOSIT);
								bankingMovementType.setDescription(movementDescription);
								bankingMovement.setBankingMovementType(bankingMovementType);
								bankingMovement.setAccountNumber(accountNumber);
								bankingMovement.setDate(Util.getToday());
								bankingMovement.setAmount(amount);
								bankingMovement.setCommision(commision);
								bankingMovement.setFinalAmount(finalAmount);

								return create(bankingMovement).flatMap(movement -> {
									LOGGER.info("Remaining amount in account: " + availableAmount.subtract(finalAmount));
									return accountProxy.updateAmount(movement.getBankProductId(), finalAmount.toString())
										.flatMap(accountUpdated -> {
											BakingMovementResponseDto bakingMovementResponse = 
												new BakingMovementResponseDto(
													movement,
											"Movement: " + movementDescription + " OK");
											return Mono.just(bakingMovementResponse);
										});
								});
							});
					}));
	}

	@Override
	public Mono<BakingMovementResponseDto> withdraw(MovementRequestDto movementRequestDto) {
		LOGGER.info("Movements withdraw: ");
		BigDecimal amount = new BigDecimal(movementRequestDto.getAmount());
		String accountNumber = movementRequestDto.getAccountNumber();
		return checkAccountNumberExists(accountNumber)
			.then(accountProxy.findByAccountNumber(accountNumber)
				.flatMap(account -> {
					return numberMovementsInCurrentMonthByAccountNumber(accountNumber)
						.flatMap(numberMovements -> {
							BigDecimal availableAmount = new BigDecimal(account.getAmount());
							LOGGER.info("Available amount in account accountNumber {}: {}", accountNumber, availableAmount);
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
							BankingMovementType bankingMovementType = new BankingMovementType();
							bankingMovementType.setId(Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL);
							String movementDescription = Util.bankingMovementTypeDescription(Constants.ID_BANK_PRODUCT_ACCOUNT_WITHDRAWAL);
							bankingMovementType.setDescription(movementDescription);
							bankingMovement.setBankingMovementType(bankingMovementType);
							bankingMovement.setDate(Util.getToday());
							bankingMovement.setAccountNumber(accountNumber);
							bankingMovement.setAmount(amount);
							bankingMovement.setCommision(commision);
							bankingMovement.setFinalAmount(finalAmount);

							return checkAvailableAmountInAccount(availableAmount, finalAmount)
								.then(create(bankingMovement).flatMap(movement -> {
									LOGGER.info("Remaining amount in account: " + availableAmount.subtract(finalAmount));
									return accountProxy.updateAmount(movement.getBankProductId(), finalAmount.multiply(new BigDecimal(-1)).toString())
										.flatMap(accountUpdated -> {
											BakingMovementResponseDto bakingMovementResponse = 
												new BakingMovementResponseDto(
													movement,
											"Movement: " + movementDescription + " OK");;
											return Mono.just(bakingMovementResponse);
										});
								}));
						});
				}));
	}

	@Override
	public Mono<BakingMovementResponseDto> pay(MovementRequestDto movementRequestDto) {
		BigDecimal amount = new BigDecimal(movementRequestDto.getAmount());
		String accountNumber = movementRequestDto.getAccountNumber();
		return checkCreditNumberExists(accountNumber)
			.then(creditProxy.findByAccountNumber(accountNumber).flatMap(credit -> {
				LOGGER.info("credit: " + credit.toString());
				BigDecimal finalAmount = amount;
				BankingMovement bankingMovement = new BankingMovement();
				bankingMovement.setBankProductId(credit.getId());

				BankingMovementType bankingMovementType = new BankingMovementType();
				bankingMovementType.setId(Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT);
				String movementDescription = Util.bankingMovementTypeDescription(Constants.ID_BANK_PRODUCT_CREDIT_PAYMENT);
				bankingMovementType.setDescription(movementDescription);
				bankingMovement.setBankingMovementType(bankingMovementType);
				bankingMovement.setAccountNumber(accountNumber);
				bankingMovement.setDate(Util.getToday());
				bankingMovement.setAmount(amount);
				bankingMovement.setFinalAmount(finalAmount);

				return create(bankingMovement).flatMap(movement -> {
					return creditProxy
						.updateAmount(movement.getBankProductId(), movement.getAmount().doubleValue())
						.flatMap(accountUpdated -> {
							BakingMovementResponseDto bakingMovementResponse = 
							new BakingMovementResponseDto(
								movement,
						"Movement: " + movementDescription + " OK");
							return Mono.just(bakingMovementResponse);
						});
				});
			}));
	}

	@Override
	public Mono<BakingMovementResponseDto> charge(MovementRequestDto movementRequestDto) {
		BigDecimal amount = new BigDecimal(movementRequestDto.getAmount());
		String accountNumber = movementRequestDto.getAccountNumber();
		return checkCreditNumberExists(accountNumber)
			.mergeWith(checkAvailableAmountInCredit(movementRequestDto))
			.then(creditProxy.findByAccountNumber(accountNumber).flatMap(credit -> {
				LOGGER.info("credit: " + credit.toString());
				BigDecimal finalAmount = amount;
				BankingMovement bankingMovement = new BankingMovement();
				bankingMovement.setBankProductId(credit.getId());

				BankingMovementType bankingMovementType = new BankingMovementType();
				bankingMovementType.setId(Constants.ID_BANK_PRODUCT_CREDIT_CHARGE);
				String movementDescription = Util.bankingMovementTypeDescription(Constants.ID_BANK_PRODUCT_CREDIT_CHARGE);
				bankingMovementType.setDescription(movementDescription);
				bankingMovement.setBankingMovementType(bankingMovementType);
				bankingMovement.setAccountNumber(accountNumber);
				bankingMovement.setDate(Util.getToday());
				bankingMovement.setAmount(amount);
				bankingMovement.setFinalAmount(finalAmount);

				return create(bankingMovement).flatMap(movement -> {
					return creditProxy
						.updateAmount(movement.getBankProductId(), movement.getAmount().doubleValue() * -1)
						.flatMap(accountUpdated -> {
							BakingMovementResponseDto bakingMovementResponse = 
								new BakingMovementResponseDto(
									movement,
							movementDescription + " OK");
							return Mono.just(bakingMovementResponse);
						});
				});
			}));
	}

	@Override
	public Flux<BakingMovementResponseDto> transfer(TransferRequestDto transferRequestDto) {
		return checkAccountNumberExists(transferRequestDto.getSourceAccountNumber())
			.mergeWith(checkAccountNumberExists(transferRequestDto.getTargetAccountNumber()))
			.thenMany(createTransfer(transferRequestDto));
	}

	@Override
	public Flux<BankingMovement> findBetweenDates(MovementRequestBetweenDatesDto movementRequestBetweenDatesDto) {
		String from = movementRequestBetweenDatesDto.getFrom();
		String to = movementRequestBetweenDatesDto.getTo();
		LocalDateTime fromDate = Util.getLocalDateTimeFromStringDate(from, "dd-MM-yyyy");
		LocalDateTime toDate = Util.getLocalDateTimeFromStringDate(to, 23, 59, "dd-MM-yyyy");
		LOGGER.info("BankingMovements between: from={} and to={}", from, to);
		return bankingMovementRepository.findByDateBetween(fromDate, toDate);
	}

	private Flux<BakingMovementResponseDto> createTransfer(TransferRequestDto transferRequestDto) {
		String sourceAccountNumber = transferRequestDto.getSourceAccountNumber();
		String targetAccountNumber = transferRequestDto.getTargetAccountNumber();
		String strAmount = transferRequestDto.getAmount();

		MovementRequestDto movementWithdraw = new MovementRequestDto();
		movementWithdraw.setAmount(strAmount);
		movementWithdraw.setAccountNumber(sourceAccountNumber);

		MovementRequestDto movementDeposit = new MovementRequestDto();
		movementDeposit.setAmount(strAmount);
		movementDeposit.setAccountNumber(targetAccountNumber);
		return Flux.concat(withdraw(movementWithdraw), deposit(movementDeposit));
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

	private Mono<Void> checkAvailableAmountInCredit(MovementRequestDto movementRequest) {
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

	private Mono<Void> checkBankAccountOrCreditNumberExists(String accountNumber) {
		return Flux.merge(getCreditByAccountNumber(accountNumber), getBankAccountByAccountNumber(accountNumber))
				.switchIfEmpty(Mono.error(new Exception("No existe ningún producto bancario con número de cuenta: " + accountNumber)))
				.then();
	}

	private Mono<Credit> getCreditByAccountNumber(String accountNumber) {
		return creditProxy.findByAccountNumber(accountNumber);
	}

	private Mono<BankAccount> getBankAccountByAccountNumber(String accountNumber) {
		return accountProxy.findByAccountNumber(accountNumber);
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
									movement.getDate().getMonthValue() == currentMonth &&
									movement.getDate().getYear() == currentYear);
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
