package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.AccountsRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountsRepo accountsRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {
        Account account = accountsRepo.findById(id);

        if (account == null) {
            if (currentUser.getRole().equals(Role.ADMIN))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!currentUser.getId().equals(account.getCustomer().getId()) && !currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping("/iban/{iban}")
    public ResponseEntity<Account> getAccountByIBAN(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("iban") String iban) {
        Account account = accountsRepo.findByIBAN(iban);

        if (account == null) {
            if (currentUser.getRole().equals(Role.ADMIN))
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (!currentUser.getId().equals(account.getCustomer().getId()) && !currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAll(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(accountsRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<Account> accounts = accountsRepo.findByCustomer_Id(id);
        if (accounts.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<Account>> getCustomerAccounts(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser) {
        if (!currentUser.getRole().equals(Role.USER)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        List<Account> accounts = accountsRepo.findByCustomer_Id(currentUser.getId());
        return new ResponseEntity<>(accounts, HttpStatus.OK);

    }

}
