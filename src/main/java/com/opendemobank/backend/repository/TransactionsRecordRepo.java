package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionsRecordRepo extends JpaRepository<TransactionRecord, Long> {
    TransactionRecord findById(long id);
}
