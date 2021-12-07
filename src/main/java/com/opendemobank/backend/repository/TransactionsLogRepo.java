package com.opendemobank.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opendemobank.backend.domain.TransactionLog;

public interface TransactionsLogRepo extends JpaRepository<TransactionLog, Long> {
}
