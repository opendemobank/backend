package com.opendemobank.backend;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DemoData {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    AccountsRepo accountsRepo;

    @Autowired
    TransactionsRepo transactionsRepo;

    @Autowired
    TransactionsRecordRepo transactionsRecordRepo;

    @Autowired
    CurrencyRepo currencyRepo;


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

        // Create initial account for in memory database during development
        Account account1 = new Account();
        account1.setIBAN("EE909900123456789013");
        account1.setAccountType(AccountType.PRIMARY);
        account1.setOpenDate(new Date(1637776275000L));
        account1.setBalance(new BigDecimal(200));
        account1.setCustomer(customer);
        accountsRepo.save(account1);

        // Create initial currency for in memory database during development
        Currency currency = new Currency();
        currency.setCode("abc");
        currency.setName("EUR");
        currency.setRate(new BigDecimal("1.0"));
        currencyRepo.save(currency);

        // Create initial transaction for in memory database during development
        Transaction transaction = new Transaction();
        transaction.setSessionUser(customer);
        transaction.setTransactionStatus(TransactionStatus.OK);
        transaction.setDescription("December's Salary");
        transaction.setLocalDateTime(LocalDateTime.now());

        //Create initial transactionRecords for in memory database during development
        TransactionRecord debitTransactionRecord = new TransactionRecord();
        debitTransactionRecord.setAccount(account);
        debitTransactionRecord.setAmount(new BigDecimal("20.0"));
        debitTransactionRecord.setDirection(Direction.DEBIT);
        debitTransactionRecord.setCurrency(currency);
        transactionsRecordRepo.save(debitTransactionRecord);

        TransactionRecord creditTransactionRecord = new TransactionRecord();
        creditTransactionRecord.setAccount(account1);
        creditTransactionRecord.setAmount(new BigDecimal("20.0"));
        creditTransactionRecord.setDirection(Direction.CREDIT);
        creditTransactionRecord.setCurrency(currency);
        transactionsRecordRepo.save(creditTransactionRecord);

        transaction.setDebitTransactionRecord(debitTransactionRecord);
        transaction.setCreditTransactionRecord(creditTransactionRecord);
        transactionsRepo.save(transaction);

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
