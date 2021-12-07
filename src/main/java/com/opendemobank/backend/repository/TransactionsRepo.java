package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.Transaction;
import com.opendemobank.backend.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionsRepo extends JpaRepository<Transaction, Long> {

    Transaction findById(long id);

    List<Transaction> findAll();

    List<Transaction> findAllBySessionUserId(long id);

    List<Transaction> findAllByLocalDateTimeBetween(LocalDateTime from, LocalDateTime to);

    List<Transaction> findAllByTransactionStatus(TransactionStatus transactionStatus);

    // List<Transaction> findAllBy_sessionUserId(long id);

}
