package com.opendemobank.backend.domain;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Collection;

@Entity
@DiscriminatorValue("admin")
public class Administrator extends User {

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + this.getId() +
                ", email='" + this.getEmail() + '\'' +
                ", password='" + this.getPassword() + '\'' +
                '}';
    }
}
