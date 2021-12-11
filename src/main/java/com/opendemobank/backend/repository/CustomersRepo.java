package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepo extends JpaRepository<Customer, Long> {
    Customer findById(long id);

    Customer findByPhoneNumber(String recieverPhoneNumber);

    Customer findByEmail(String recieverEmail);
}
