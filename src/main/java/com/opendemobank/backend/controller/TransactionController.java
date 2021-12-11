package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.manager.TransactionManagers;
import com.opendemobank.backend.repository.TransactionsRepo;
import com.opendemobank.backend.repository.UsersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    TransactionsRepo transactionsRepo;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    TransactionManagers transactionManagers;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @PathVariable("id") long id) {

        // find transaction by id
        Transaction transaction = transactionsRepo.findById(id);

        // if there's no such transaction
        if (transaction == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // only return the transaction when it was created by the logged-in user or an admin
        if (Objects.equals(currentUser.getId(), transaction.getSessionUser().getId()) || currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(transaction, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(transactionsRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Transaction>> getAllBySessionUserId(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @PathVariable("id") long id) {
        User user = usersRepo.findById(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (currentUser.getId() != id && currentUser.getRole() != Role.ADMIN)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<Transaction> transactions = transactionsRepo.findAllBySessionUserId(id);

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> createTransaction(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @RequestBody TransactionManagers.NewTransactionForm form) {
        // if currentUser is not admin, return auth fail
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return transactionManagers.createTransaction(currentUser, form);
    }


    // POST edit transaction
    @PostMapping(value = "/edit/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> editTransaction(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @RequestBody TransactionManagers.EditTransactionForm form) {
        // if currentUser is not admin, return auth fail
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return transactionManagers.editTransaction(currentUser, form);
    }

    // POST storno transaction
    @PostMapping(value = "/storno/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transaction> stornoTransaction(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @RequestBody TransactionManagers.StronoTransactionForm form) {
        // if currentUser is not admin, return auth fail
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return transactionManagers.stornoTransaction(currentUser, form);
    }

}
