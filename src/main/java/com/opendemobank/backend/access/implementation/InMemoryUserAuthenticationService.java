package com.opendemobank.backend.access.implementation;

import com.opendemobank.backend.access.UserAuthenticationService;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.UsersRepo;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class InMemoryUserAuthenticationService implements UserAuthenticationService {

    @Autowired
    UsersRepo usersRepo;

    private Map<String, User> tokenMap = new HashMap<>();

    @Override
    public Optional<String> login(String email, String password) {
        User user = usersRepo.findUserByEmail(email);

        if (user == null || !user.isActive()) return Optional.empty();

        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            logout(user);
            String token = UUID.randomUUID().toString();
            tokenMap.put(token, user);
            return Optional.of(token);
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByToken(String token) {
        if (tokenMap.containsKey(token)) return Optional.of(tokenMap.get(token));
        return Optional.empty();
    }

    @Override
    public void logout(User user) {
        tokenMap.values().remove(user);
    }
}
