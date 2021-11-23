package com.opendemobank.backend;

import com.opendemobank.backend.domain.Administrator;
import com.opendemobank.backend.domain.Customer;
import com.opendemobank.backend.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoData {

    @Autowired
    UsersRepo usersRepo;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        // Create initial admin for in memory database during development
        Administrator admin = new Administrator();
        admin.setEmail("admin@opendemobank.com");
        admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
        usersRepo.save(admin);

        // Create initial customer for in memory database during development
        Customer customer = new Customer();
        customer.setEmail("customer@opendemobank.com");
        customer.setPassword(new BCryptPasswordEncoder().encode("customer"));
        customer.setFullName("Paul Pähkel");
        customer.setPhoneNumber("+358 40 1234567");
        usersRepo.save(customer);
    }
}
