package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.Customer;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.AccountsRepo;
import com.opendemobank.backend.repository.CustomersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    AccountsRepo accountsRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @PathVariable("id") long id) {

        if (currentUser.getId() != id && !currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Customer customer = customersRepo.findById(id);
        if (customer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAll(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(customersRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Customer> createCustomer(
            @Parameter(hidden = true) @AuthenticationPrincipal final User currentUser,
            @Valid @RequestBody Customer customer,
            @Parameter(hidden = true) BindingResult bindingResult) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        customer.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        customersRepo.saveAndFlush(customer);
        for (Account account : customer.getAccounts()) {
            account.setCustomer(customer);
            account.setIBAN(Account.generateIBAN(account.getId()));
        }
        return new ResponseEntity<>(customersRepo.save(customer), HttpStatus.CREATED);
    }
}
