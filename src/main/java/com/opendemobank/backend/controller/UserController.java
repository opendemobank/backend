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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<User> addAdmin(
            @Parameter(hidden = true) @AuthenticationPrincipal final User currentUser,
            @Valid @RequestBody Administrator user,
            @Parameter(hidden = true) BindingResult bindingResult) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return new ResponseEntity<>(usersRepo.save(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {
        if (!currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        User user = usersRepo.findById(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        user.setActive(false);
        return new ResponseEntity<>(usersRepo.save(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam("email") final String email, @RequestParam("password") final String password) {
        final String token = authentication
                .login(email, password).orElse(null);

        if (token == null)
            return new ResponseEntity<>("Invalid email and/or password.", HttpStatus.UNAUTHORIZED);

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", authentication.findByToken(token));

        return new ResponseEntity<>(map, HttpStatus.OK);
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
