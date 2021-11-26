package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionsRepo extends JpaRepository<Transaction, Long> {

    Transaction getById(long id);

    List<Transaction> getAll();

    List<Transaction> getByAccountId(long id);

    List<Transaction> getByAccount(Account account);

    Boolean add(Transaction transaction);

    Boolean update(Transaction transaction);

}
