package com.nttdata.lagm.movements.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.lagm.movements.model.BankingMovement;

import reactor.core.publisher.Flux;

@Repository
public interface BankingMovementRepository extends ReactiveMongoRepository<BankingMovement, String> {
    Flux<BankingMovement> findByDateBetween(LocalDateTime from, LocalDateTime to);
}
