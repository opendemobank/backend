package com.opendemobank.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User sessionUser;

    @Column(name = "description")
    private String description;

    @Column(name = "receivers_full_name")
    private String receiversFullName;

    @Column(name = "account_iban")
    private String accountIBAN;

    @Column(name = "amount")
    private BigDecimal amount;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiversFullName() {
        return receiversFullName;
    }

    public void setReceiversFullName(String receiversFullName) {
        this.receiversFullName = receiversFullName;
    }

    public String getAccountIBAN() {
        return accountIBAN;
    }

    public void setAccountIBAN(String accountIBAN) {
        this.accountIBAN = accountIBAN;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
