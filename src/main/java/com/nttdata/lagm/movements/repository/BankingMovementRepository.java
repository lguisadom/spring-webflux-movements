package com.nttdata.lagm.movements.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.nttdata.lagm.movements.model.BankingMovement;

@Repository
public interface BankingMovementRepository extends ReactiveMongoRepository<BankingMovement, Long>{

}