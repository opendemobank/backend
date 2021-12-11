package com.opendemobank.backend.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("admin")
public class Administrator extends User {

    @Override
    public Role getRole() {
        if (super.isActive())
            return Role.ADMIN;
        else
            return Role.INACTIVE;
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
