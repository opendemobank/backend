package com.opendemobank.backend.manager;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionManagers {

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


    public ResponseEntity<Transaction> createTransaction(User currentUser, NewTransactionForm form) {
        Account originAccount = accountsRepo.findByIBAN(form.getOriginIban());
        Account destinationAccount = accountsRepo.findByIBAN(form.getEndIban());

        if (destinationAccount == null || !destinationAccount.isActive() || !destinationAccount.getCustomer().isActive()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (originAccount != null && (!originAccount.isActive() || !originAccount.getCustomer().isActive())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Create debit transaction record
        TransactionRecord debitTransaction = new TransactionRecord();
        debitTransaction.setAmount(form.getAmount());
        if (originAccount != null) {
            debitTransaction.setCurrency(originAccount.getCurrency());
        } else {
            debitTransaction.setCurrency(currencyRepo.findByCode("EUR"));
        }
        debitTransaction.setAccount(originAccount);
        debitTransaction.setDirection(Direction.DEBIT);
        transactionsRecordRepo.saveAndFlush(debitTransaction);

        BigDecimal receiverAmount = Currency.convert(
                form.getAmount(),
                debitTransaction.getCurrency().getRate(),
                destinationAccount.getCurrency().getRate());


        // Create credit transaction record
        TransactionRecord creditTransaction = new TransactionRecord();
        creditTransaction.setCurrency(destinationAccount.getCurrency());
        creditTransaction.setAmount(receiverAmount);
        creditTransaction.setAccount(destinationAccount);
        creditTransaction.setDirection(Direction.CREDIT);
        transactionsRecordRepo.saveAndFlush(creditTransaction);

        // Create transfer
        Transfer transfer = new Transfer();
        transfer.setSessionUser(currentUser);
        transfer.setDescription(form.getDescription());
        transfer.setReceiversFullName(destinationAccount.getCustomer().getFullName());
        transfer.setAccountIBAN(destinationAccount.getIBAN());
        transfer.setAmount(form.getAmount());
        transfersRepo.saveAndFlush(transfer);


        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setDebitTransactionRecord(debitTransaction);
        transaction.setCreditTransactionRecord(creditTransaction);
        transaction.setTransactionStatus(TransactionStatus.OK);
        transaction.setDescription(form.getDescription());
        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setSessionUser(currentUser);
        transaction.setTransfer(transfer);
        transactionsRepo.saveAndFlush(transaction);

        // Update records with new transaction
        debitTransaction.setTransaction(transaction);
        creditTransaction.setTransaction(transaction);
        transactionsRecordRepo.saveAndFlush(debitTransaction);
        transactionsRecordRepo.saveAndFlush(creditTransaction);

        if (originAccount != null) {
            originAccount.setBalance(originAccount.getBalance().subtract(debitTransaction.getAmount()));
            accountsRepo.saveAndFlush(originAccount);
        }

        destinationAccount.setBalance(destinationAccount.getBalance().add(creditTransaction.getAmount()));
        accountsRepo.saveAndFlush(destinationAccount);

        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    public ResponseEntity<Transaction> editTransaction(User currentUser, EditTransactionForm form) {
        // Check if transaction with id exists
        Optional<Transaction> transactionOpt = transactionsRepo.findById(form.getId());

        if (transactionOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Transaction transaction = transactionOpt.get();

        // Check if transaction already corrected
        if (transaction.getTransactionStatus() != TransactionStatus.OK) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Edit transaction timestamp and amount
        transaction.setLocalDateTime(form.getLocalDateTime());
        transaction.setTransactionStatus(TransactionStatus.CORECTION);

        // Get debit and credit transaction records
        TransactionRecord debitTransaction = transaction.getDebitTransactionRecord();
        TransactionRecord creditTransaction = transaction.getCreditTransactionRecord();

        // Get old amounts
        BigDecimal oldDebitAmount = debitTransaction.getAmount();
        BigDecimal oldCreditAmount = creditTransaction.getAmount();

        // Modify amounts
        debitTransaction.setAmount(form.getAmount());

        BigDecimal receiverAmount = Currency.convert(
                form.getAmount(),
                debitTransaction.getCurrency().getRate(),
                creditTransaction.getCurrency().getRate());

        creditTransaction.setAmount(receiverAmount);

        // Get accounts
        Account debitAccount = debitTransaction.getAccount();
        Account creditAccount = creditTransaction.getAccount();

        // Update account balances
        creditAccount.setBalance(creditAccount.getBalance().subtract(oldCreditAmount).add(creditTransaction.getAmount()));
        debitAccount.setBalance(debitAccount.getBalance().add(oldDebitAmount).subtract(debitTransaction.getAmount()));

        // Save changes
        transactionsRecordRepo.saveAndFlush(debitTransaction);
        transactionsRecordRepo.saveAndFlush(creditTransaction);
        accountsRepo.saveAndFlush(debitAccount);
        accountsRepo.saveAndFlush(creditAccount);
        transactionsRepo.saveAndFlush(transaction);

        // return transaction
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    public ResponseEntity<Transaction> stornoTransaction(User currentUser, StronoTransactionForm form) {
        // Check if transaction with id exists
        Optional<Transaction> transactionOpt = transactionsRepo.findById(form.getId());

        // Check if transaction exists
        if (transactionOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Transaction transaction = transactionOpt.get();

        // Check if transaction status is OK or CORECTION
        if (transaction.getTransactionStatus() != TransactionStatus.OK && transaction.getTransactionStatus() != TransactionStatus.CORECTION) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Set transaction status to STORNED
        transaction.setTransactionStatus(TransactionStatus.STORNED);
        transactionsRepo.saveAndFlush(transaction);

        // Make new transaction reversing old transaction
        Transaction newTransaction = new Transaction();
        newTransaction.setLocalDateTime(transaction.getLocalDateTime());
        newTransaction.setTransactionStatus(TransactionStatus.STORNO);
        newTransaction.setSessionUser(currentUser);
        newTransaction.setDescription("Storno of " + transaction.getId());
        transactionsRepo.saveAndFlush(newTransaction);

        // Create new credit and debit transaction records
        TransactionRecord debitTransaction = new TransactionRecord();
        TransactionRecord creditTransaction = new TransactionRecord();

        // Set new transaction records
        debitTransaction.setAmount(transaction.getCreditTransactionRecord().getAmount());
        debitTransaction.setAccount(transaction.getCreditTransactionRecord().getAccount());
        debitTransaction.setCurrency(transaction.getCreditTransactionRecord().getCurrency());
        debitTransaction.setTransaction(newTransaction);
        debitTransaction.setDirection(Direction.DEBIT);

        creditTransaction.setAmount(transaction.getDebitTransactionRecord().getAmount());
        creditTransaction.setAccount(transaction.getDebitTransactionRecord().getAccount());
        creditTransaction.setCurrency(transaction.getDebitTransactionRecord().getCurrency());
        creditTransaction.setTransaction(newTransaction);
        creditTransaction.setDirection(Direction.CREDIT);


        // Save new transaction records
        transactionsRecordRepo.saveAndFlush(debitTransaction);
        transactionsRecordRepo.saveAndFlush(creditTransaction);

        transaction.setDebitTransactionRecord(creditTransaction);
        transaction.setDebitTransactionRecord(debitTransaction);

        // Save transaction
        transactionsRepo.saveAndFlush(newTransaction);

        // Get accounts
        Account debitAccount = debitTransaction.getAccount();
        Account creditAccount = creditTransaction.getAccount();

        // Update account balances
        creditAccount.setBalance(creditAccount.getBalance().add(creditTransaction.getAmount()));
        debitAccount.setBalance(debitAccount.getBalance().subtract(debitTransaction.getAmount()));

        // Save changes
        accountsRepo.saveAndFlush(debitAccount);
        accountsRepo.saveAndFlush(creditAccount);

        // Return transaction
        return new ResponseEntity<>(newTransaction, HttpStatus.OK);
    }

    public static class NewTransactionForm {
        public String description;
        public String originIban;
        public String endIban;
        public BigDecimal amount;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getOriginIban() {
            return originIban;
        }

        public void setOriginIban(String originIban) {
            this.originIban = originIban;
        }

        public String getEndIban() {
            return endIban;
        }

        public void setEndIban(String endIban) {
            this.endIban = endIban;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class EditTransactionForm {
        public Long id;
        public BigDecimal amount;
        public LocalDateTime localDateTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }
    }

    public static class StronoTransactionForm {
        public Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
