package com.opendemobank.backend.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    // TODO: sessionUser

    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "description")
    private String description;

    @Column(name = "local_date_time")
    private LocalDateTime localDateTime;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionRecord> transactionRecords;

    public long getId() {
        return id;
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

    public List<TransactionRecord> getTransactionRecords() {
        return transactionRecords;
    }

    public void setTransactionRecords(List<TransactionRecord> transactionRecords) {
        this.transactionRecords = transactionRecords;
    }
}
