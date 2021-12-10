package com.opendemobank.backend.manager;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

        if (destinationAccount == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TransactionRecord debitTransaction = new TransactionRecord();

        debitTransaction.setAmount(form.getAmount());
        debitTransaction.setCurrency(currencyRepo.getById(1L)); // TODO fix currency
        debitTransaction.setAccount(originAccount);
        debitTransaction.setDirection(Direction.DEBIT);
        transactionsRecordRepo.saveAndFlush(debitTransaction);

        TransactionRecord creditTransaction = new TransactionRecord();
        creditTransaction.setCurrency(currencyRepo.getById(1L)); // TODO fix currency
        creditTransaction.setAmount(form.getAmount());
        creditTransaction.setAccount(destinationAccount);
        creditTransaction.setDirection(Direction.CREDIT);
        transactionsRecordRepo.saveAndFlush(creditTransaction);

        Transfer transfer = new Transfer();
        transfer.setSessionUser(destinationAccount.getCustomer());
        transfer.setDescription(form.getDescription());
        transfer.setReceiversFullName(destinationAccount.getCustomer().getFullName());
        transfer.setAccountIBAN(destinationAccount.getIBAN());
        transfer.setAmount(form.getAmount());
        transfersRepo.saveAndFlush(transfer);

        Transaction transaction = new Transaction();
        transaction.setDebitTransactionRecord(debitTransaction);
        transaction.setCreditTransactionRecord(creditTransaction);
        transaction.setTransactionStatus(TransactionStatus.OK);
        transaction.setDescription(form.getDescription());
        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setSessionUser(currentUser);
        transaction.setTransfer(transfer);
        transactionsRepo.saveAndFlush(transaction);

        if (originAccount != null) {
            originAccount.setBalance(originAccount.getBalance().subtract(form.getAmount()));
            accountsRepo.saveAndFlush(originAccount);
        }

        destinationAccount.setBalance(destinationAccount.getBalance().add(form.getAmount()));
        accountsRepo.saveAndFlush(destinationAccount);

        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
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
}
