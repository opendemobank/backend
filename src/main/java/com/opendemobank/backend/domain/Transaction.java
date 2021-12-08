package com.opendemobank.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User sessionUser;

    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "local_date_time")
    private LocalDateTime localDateTime;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "transaction")
    private TransactionRecord creditTransactionRecord;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "transaction")
    private TransactionRecord debitTransactionRecord;

    @OneToOne
    @JoinColumn(name = "transfer")
    private Transfer transfer;

    public long getId() {
        return id;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return localDateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.localDateTime = dateTime;
    }

    public TransactionRecord getDebitTransactionRecord() {
        return debitTransactionRecord;
    }

    public void setDebitTransactionRecord(TransactionRecord debitTransactionRecord) {
        this.debitTransactionRecord = debitTransactionRecord;
    }

    public TransactionRecord getCreditTransactionRecord() {
        return creditTransactionRecord;
    }

    public void setCreditTransactionRecord(TransactionRecord creditTransactionRecord) {
        this.creditTransactionRecord = creditTransactionRecord;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }
}
