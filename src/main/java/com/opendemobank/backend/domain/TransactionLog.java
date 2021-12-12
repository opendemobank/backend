package com.opendemobank.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_log")
public class TransactionLog {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User sessionUser;

    @Column(name = "local_date_time")
    private LocalDateTime localDateTime = LocalDateTime.now();

    @Column(name = "old_json", columnDefinition = "CLOB")
    @Lob
    private String oldJson;

    @Column(name = "new_json", columnDefinition = "CLOB")
    @Lob
    private String newJson;

    public Long getId() {
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

    public String getOldJson() {
        return oldJson;
    }

    public void setOldJson(String oldJson) {
        this.oldJson = oldJson;
    }

    public String getNewJson() {
        return newJson;
    }

    public void setNewJson(String newJson) {
        this.newJson = newJson;
    }
}
