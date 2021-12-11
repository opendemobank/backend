package com.opendemobank.backend;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.manager.TransactionManagers;
import com.opendemobank.backend.repository.*;
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

    @Autowired
    TransfersRepo transfersRepo;

    @Autowired
    TransactionsRepo transactionsRepo;

    @Autowired
    TransactionsRecordRepo transactionsRecordRepo;

    @Autowired
    CurrencyRepo currencyRepo;

    @Autowired
    TransactionManagers transactionManagers;

    @EventListener
    public void appReady(ApplicationReadyEvent event) {
        // Create initial currency for in memory database during development
        Currency currency1 = new Currency();
        currency1.setCode("USD");
        currency1.setName("US Dollar");
        currency1.setRate(new BigDecimal("1.1273"));
        currency1.setSymbol("¥");
        currencyRepo.save(currency1);

        Currency currency2 = new Currency();
        currency2.setCode("CNY");
        currency2.setName("Chinese yuan renminbi");
        currency2.setRate(new BigDecimal("7.1814"));
        currency2.setSymbol("$");
        currencyRepo.save(currency2);

        Currency currency3 = new Currency();
        currency3.setCode("EUR");
        currency3.setName("Euro");
        currency3.setRate(new BigDecimal("1.0"));
        currency3.setSymbol("€");
        currencyRepo.save(currency3);

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
        account.setIBAN(Account.generateIBAN(1L));
        account.setAccountType(AccountType.PRIMARY);
        account.setOpenDate(new Date(1637776275000L));
        account.setBalance(new BigDecimal(100));
        account.setCustomer(customer);
        account.setCurrency(currency3);
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
        account2.setIBAN(Account.generateIBAN(2L));
        account2.setAccountType(AccountType.PRIMARY);
        account2.setOpenDate(new Date(1637776875000L));
        account2.setBalance(new BigDecimal(50));
        account2.setCustomer(customer2);
        account2.setCurrency(currency1);
        accountsRepo.save(account2);

        TransactionManagers.NewTransactionForm newTransaction = new TransactionManagers.NewTransactionForm();
        newTransaction.setDescription("December's Salary");
        newTransaction.setAmount(new BigDecimal("20.0"));
        newTransaction.setOriginIban(account.getIBAN());
        newTransaction.setEndIban(account2.getIBAN());
        transactionManagers.createTransaction(customer, newTransaction);
    }
}
