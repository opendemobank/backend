package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Currency;
import com.opendemobank.backend.repository.CurrencyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    CurrencyRepo currencyRepo;

    // Return all currencies
    @GetMapping
    public ResponseEntity<List<Currency>> getAll() {
        return new ResponseEntity<>(currencyRepo.findAll(), HttpStatus.OK);
    }
}
