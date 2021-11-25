package com.opendemobank.backend;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.AccountType;
import com.opendemobank.backend.domain.Administrator;
import com.opendemobank.backend.domain.Customer;
import com.opendemobank.backend.repository.AccountsRepo;
import com.opendemobank.backend.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class DemoData {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    AccountsRepo accountsRepo;

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

        // Create initial account for in memory database during development
        Account account = new Account();
        account.setIBAN("EE909900123456789012");
        account.setAccountType(AccountType.PRIMARY);
        account.setOpenDate(new Date(1637776275000L));
        account.setBalance(new BigDecimal(100));
        account.setCustomer(customer);
        accountsRepo.save(account);

        // Create second customer for in memory database during development
        Customer customer2 = new Customer();
        customer2.setEmail("customer2@opendemobank.com");
        customer2.setPassword(new BCryptPasswordEncoder().encode("customer2"));
        customer2.setFullName("Paulus Kivi");
        customer2.setPhoneNumber("+358 40 9876543");
        usersRepo.save(customer2);

        // Create second account for in memory database during development
        Account account2 = new Account();
        account2.setIBAN("EE909900123456700000");
        account2.setAccountType(AccountType.PRIMARY);
        account2.setOpenDate(new Date(1637776875000L));
        account2.setBalance(new BigDecimal(50));
        account2.setCustomer(customer2);
        accountsRepo.save(account2);
    }
}
