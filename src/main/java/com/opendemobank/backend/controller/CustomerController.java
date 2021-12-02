package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Customer;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.repository.CustomersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    CustomersRepo customersRepo;

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
    public ResponseEntity<Customer> createCustomer(@Parameter(hidden = true) @AuthenticationPrincipal final User currentUser, @RequestBody Customer customer) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        customer.setPassword(new BCryptPasswordEncoder().encode(customer.getPassword()));
        return new ResponseEntity<>(customersRepo.save(customer), HttpStatus.CREATED);
    }
}
