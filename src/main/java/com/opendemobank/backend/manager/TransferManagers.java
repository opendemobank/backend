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
public class TransferManagers {

    @Autowired
    TransfersRepo transfersRepo;

    @Autowired
    UsersRepo usersRepo;
    
    @Autowired
    AccountsRepo accountsRepo;
    
    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    TransactionsRecordRepo transactionsRecordRepo;

    @Autowired
    TransactionsRepo transactionsRepo;



    public ResponseEntity<Transfer> createTransfer(User currentUser, CreateTransferForm form) {
        // Get Sender Account by Iban
        Account senderAccount = accountsRepo.findByIBAN(form.getSenderIBAN());
        
        // Check if account exists
        if (senderAccount == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Check if user is owner of account
        if (!senderAccount.getCustomer().getId().equals(currentUser.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Check if owner account has enough balance
        if (senderAccount.getBalance().compareTo(form.getAmount()) < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Optional<Account> recipientAccount = Optional.empty();
        
        // Get Receiver account
        if (form.getRecieverIBAN() != null) {
            recipientAccount = Optional.ofNullable(accountsRepo.findByIBAN(form.getRecieverIBAN()));
        } else if (form.getRecieverPhoneNumber() != null) {
            Customer customer = customersRepo.findByPhoneNumber(form.getRecieverPhoneNumber());
            // Find primary account
            if (customer != null) {
                recipientAccount = customer.getAccounts().stream().filter(account -> account.getAccountType().equals(AccountType.PRIMARY)).findFirst();
            }
        } else if (form.getRecieverEmail() != null) {
            Customer customer = customersRepo.findByEmail(form.getRecieverEmail());
            // Find primary account
            if (customer != null) {
                recipientAccount = customer.getAccounts().stream().filter(account -> account.getAccountType().equals(AccountType.PRIMARY)).findFirst();
            }
        }
        
        // Check if account exists
        if (!recipientAccount.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Create debit transaction record
        TransactionRecord debitTransactionRecord = new TransactionRecord();
        debitTransactionRecord.setAmount(form.getAmount());
        debitTransactionRecord.setAccount(senderAccount);
        debitTransactionRecord.setDirection(Direction.DEBIT);
        debitTransactionRecord.setCurrency(senderAccount.getCurrency());
        transactionsRecordRepo.saveAndFlush(debitTransactionRecord);

        // Create credit transaction record
        TransactionRecord creditTransactionRecord = new TransactionRecord();
        creditTransactionRecord.setAmount(form.getAmount());
        creditTransactionRecord.setAccount(recipientAccount.get());
        creditTransactionRecord.setDirection(Direction.CREDIT);
        creditTransactionRecord.setCurrency(recipientAccount.get().getCurrency());
        transactionsRecordRepo.saveAndFlush(creditTransactionRecord);

        // Create transfer
        Transfer transfer = new Transfer();
        transfer.setSessionUser(currentUser);
        transfer.setDescription(form.getDescription());
        transfer.setReceiversFullName(recipientAccount.get().getCustomer().getFullName());
        transfer.setAccountIBAN(recipientAccount.get().getIBAN());
        transfer.setAmount(form.getAmount());
        transfersRepo.saveAndFlush(transfer);

        // Update sender account balance
        senderAccount.setBalance(senderAccount.getBalance().subtract(form.getAmount()));
        accountsRepo.saveAndFlush(senderAccount);

        // Update receiver account balance
        recipientAccount.get().setBalance(recipientAccount.get().getBalance().add(form.getAmount()));
        accountsRepo.saveAndFlush(recipientAccount.get());

        // Create Transaction
        Transaction transaction = new Transaction();
        transaction.setDebitTransactionRecord(debitTransactionRecord);
        transaction.setCreditTransactionRecord(creditTransactionRecord);
        transaction.setTransactionStatus(TransactionStatus.OK);
        transaction.setDescription(form.getDescription());
        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setSessionUser(currentUser);
        transaction.setTransfer(transfer);
        transactionsRepo.saveAndFlush(transaction);

        // Update records with new transaction
        debitTransactionRecord.setTransaction(transaction);
        creditTransactionRecord.setTransaction(transaction);
        transactionsRecordRepo.saveAndFlush(debitTransactionRecord);
        transactionsRecordRepo.saveAndFlush(creditTransactionRecord);

        // Return new transfer
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }


    public static class CreateTransferForm {
        public String senderIBAN;
        public String description;
        public BigDecimal amount;

        public String recieverPhoneNumber;
        public String recieverEmail;
        public String recieverIBAN;

        public String getSenderIBAN() {
            return senderIBAN;
        }

        public void setSenderIBAN(String senderIBAN) {
            this.senderIBAN = senderIBAN;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getRecieverPhoneNumber() {
            return recieverPhoneNumber;
        }

        public void setRecieverPhoneNumber(String recieverPhoneNumber) {
            this.recieverPhoneNumber = recieverPhoneNumber;
        }

        public String getRecieverEmail() {
            return recieverEmail;
        }

        public void setRecieverEmail(String recieverEmail) {
            this.recieverEmail = recieverEmail;
        }

        public String getRecieverIBAN() {
            return recieverIBAN;
        }

        public void setRecieverIBAN(String recieverIBAN) {
            this.recieverIBAN = recieverIBAN;
        }
    }
}
