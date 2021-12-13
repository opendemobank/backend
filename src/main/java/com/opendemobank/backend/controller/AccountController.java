package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.*;
import com.opendemobank.backend.repository.AccountsRepo;
import com.opendemobank.backend.repository.CurrencyRepo;
import com.opendemobank.backend.repository.CustomersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    AccountsRepo accountsRepo;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    CurrencyRepo currencyRepo;

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

    @PostMapping("/customer/{id}")
    public ResponseEntity<Account> addAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal final User currentUser,
            @PathVariable("id") long id) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Customer customer = customersRepo.findById(id);
        if (customer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Account account = new Account();
        account.setCurrency(currencyRepo.findByCode("EUR"));
        account.setBalance(new BigDecimal("0.0"));
        account.setCustomer(customer);

        // If there is already a primary account bound to customer
        if (customer.getAccounts().stream().anyMatch(a -> a.getAccountType() == AccountType.PRIMARY))
            account.setAccountType(AccountType.SECONDARY);
        else
            account.setAccountType(AccountType.PRIMARY);

        Account newAccount = accountsRepo.saveAndFlush(account);
        newAccount.setIBAN(Account.generateIBAN(account.getId()));

        return new ResponseEntity<>(accountsRepo.save(newAccount), HttpStatus.CREATED);
    }

    @PostMapping("/customer/{id}/new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Account> createAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal final User currentUser,
            @PathVariable("id") long id,
            @Valid @RequestBody Account account,
            @Parameter(hidden = true) BindingResult bindingResult) {

        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Customer customer = customersRepo.findById(id);
        if (customer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        account.setCustomer(customer);
        Account newAccount = accountsRepo.saveAndFlush(account);
        newAccount.setIBAN(Account.generateIBAN(account.getId()));

        return new ResponseEntity<>(accountsRepo.save(newAccount), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Account> deleteAccount(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {
        if (!currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Account account = accountsRepo.findById(id);
        if (account == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        account.setActive(false);
        return new ResponseEntity<>(accountsRepo.save(account), HttpStatus.OK);
    }
}
