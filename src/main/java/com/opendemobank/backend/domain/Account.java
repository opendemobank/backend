package com.opendemobank.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "iban", unique = true)
    private String IBAN;

    @NotNull
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "open_date")
    private Date openDate = new Date();

    @NotNull
    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account", cascade = CascadeType.ALL)
    private List<TransactionRecord> transactionRecords;

    @JsonIgnore
    @Column(name = "active")
    private boolean active = true;

    public long getId() {
        return id;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public AccountType getAccountType() {
        if (isActive())
            return accountType;
        else
            return AccountType.INACTIVE;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<TransactionRecord> getTransactionRecords() {
        return transactionRecords;
    }

    public void setTransactionRecords(List<TransactionRecord> transactionRecords) {
        this.transactionRecords = transactionRecords;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @JsonIgnore
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", IBAN='" + IBAN + '\'' +
                ", accountType=" + accountType +
                ", openDate=" + openDate +
                ", balance=" + balance +
                ", customer=" + customer +
                '}';
    }

    public static String generateIBAN(Long id) {
        return new Iban.Builder()
                .countryCode(CountryCode.EE)
                .bankCode("11")
                .branchCode("00")
                .accountNumber(String.format("%011d", id))
                .nationalCheckDigit("5")
                .build().toString();
    }
}
