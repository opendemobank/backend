package com.opendemobank.backend.domain;

import javax.persistence.*;

@Entity
@Table(name = "transactionlog")
public class TransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long logid;

    public long getLogid() {
        return logid;
    }

    public void setLogid(long logid) {
        this.logid = logid;
    }
}
