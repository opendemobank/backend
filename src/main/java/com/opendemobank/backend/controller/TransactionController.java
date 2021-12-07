package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Customer;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.Transaction;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.TransactionsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    TransactionsRepo transactionsRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@AuthenticationPrincipal User currentUser, @PathVariable("id") long id) {

        if (currentUser.getId() != id && !currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Transaction transaction = transactionsRepo.findById(id);
        if (transaction == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(@AuthenticationPrincipal User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(transactionsRepo.findAll(), HttpStatus.OK);
    }


    // TODO Postmappings

}
