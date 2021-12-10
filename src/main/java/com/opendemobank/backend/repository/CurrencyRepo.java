package com.opendemobank.backend.repository;


import com.opendemobank.backend.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepo extends JpaRepository<Currency, Long> {
}
