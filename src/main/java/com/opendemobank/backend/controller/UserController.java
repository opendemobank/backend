package com.opendemobank.backend.controller;

import com.opendemobank.backend.access.UserAuthenticationService;
import com.opendemobank.backend.domain.Administrator;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.UsersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserAuthenticationService authentication;

    @Autowired
    UsersRepo usersRepo;


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {
        if (currentUser.getId() != id && !currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User requestedUser = usersRepo.findById(id);
        if (requestedUser == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(requestedUser, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(usersRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<User> addAdmin(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @RequestBody Administrator user) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return new ResponseEntity<>(usersRepo.save(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    String login(@RequestParam("email") final String email, @RequestParam("password") final String password) {
        return authentication
                .login(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid email and/or password."));
    }

    @GetMapping("/current")
    User getCurrent(@AuthenticationPrincipal final User currentUser) {
        return currentUser;
    }

    @GetMapping("/logout")
    boolean logout(@AuthenticationPrincipal final User currentUser) {
        authentication.logout(currentUser);
        return true;
    }
}
