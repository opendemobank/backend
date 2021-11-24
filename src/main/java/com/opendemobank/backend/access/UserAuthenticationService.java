package com.opendemobank.backend.access;

import com.opendemobank.backend.domain.User;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String email, String password);

    Optional<User> findByToken(String token);

    void logout(User user);
}
