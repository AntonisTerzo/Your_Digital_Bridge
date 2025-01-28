package dev.antonis.your_digital_bridge.transaction;

import dev.antonis.your_digital_bridge.entity.Transaction;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends ListCrudRepository<Transaction, Integer> {
} 