package com.nttdata.lagm.movements.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.lagm.movements.model.BankingMovement;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface BankingMovementRepository extends ReactiveMongoRepository<BankingMovement, String> {
    Flux<BankingMovement> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
