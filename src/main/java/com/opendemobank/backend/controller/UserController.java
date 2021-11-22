package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Administrator;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UsersRepo usersRepo;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") long id) {
        User user = usersRepo.findById(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return new ResponseEntity<>(usersRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<User> addAdmin(@RequestBody Administrator user) {
        return new ResponseEntity<>(usersRepo.save(user), HttpStatus.CREATED);
    }

}
