package com.opendemobank.backend.repository;

import com.opendemobank.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<User, Long> {
    User findById(long id);

    User findUserByEmail(String email);
}
