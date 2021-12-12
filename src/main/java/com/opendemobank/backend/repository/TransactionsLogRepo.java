package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import com.opendemobank.backend.domain.Transaction;

public interface TransactionsLogRepo extends JpaRepository<TransactionLog, Long> {
    TransactionLog findById(long id);
}
