package com.opendemobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opendemobank.backend.domain.Transaction;

public interface TransactionsLogRepo extends JpaRepository<Transaction, Long> {
    Transaction findById(long id);
}
