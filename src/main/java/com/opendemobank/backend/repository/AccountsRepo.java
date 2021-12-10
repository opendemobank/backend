package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountsRepo extends JpaRepository<Account, Long> {
    Account findById(long id);

    Account findByIBAN(String iban);

    List<Account> findByCustomer_Id(long id);
}
