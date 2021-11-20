package com.opendemobank.backend.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findById(long id);
}
