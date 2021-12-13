package com.opendemobank.backend.controller;

import com.opendemobank.backend.domain.Account;
import com.opendemobank.backend.domain.Role;
import com.opendemobank.backend.domain.Transfer;
import com.opendemobank.backend.domain.User;
import com.opendemobank.backend.manager.TransferManagers;
import com.opendemobank.backend.repository.AccountsRepo;
import com.opendemobank.backend.repository.TransfersRepo;
import com.opendemobank.backend.repository.UsersRepo;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    TransfersRepo transfersRepo;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    AccountsRepo accountsRepo;

    @Autowired
    TransferManagers transferManager;

    @GetMapping("/{id}")
    public ResponseEntity<Transfer> getById(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @PathVariable("id") long id) {

        // find transfer by id
        Transfer transfer = transfersRepo.findById(id);

        // if there's no such transfer
        if (transfer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // only return the transfer when receiver or sender was a part of the transaction or an admin
        if (Objects.equals(currentUser.getId(), transfer.getSessionUser().getId()) || currentUser.getRole().equals(Role.ADMIN))
            return new ResponseEntity<>(transfer, HttpStatus.OK);

        // only return the transfer when receiver or sender was a part of the transaction or an admin
        if (transfer.getReceiverUser() != null && Objects.equals(currentUser.getId(), transfer.getReceiverUser().getId()))
            return new ResponseEntity<>(transfer, HttpStatus.OK);

        // only return the transfer when receiver or sender was a part of the transaction or an admin
        if (transfer.getSenderUser() != null && Objects.equals(currentUser.getId(), transfer.getSenderUser().getId()))
            return new ResponseEntity<>(transfer, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<Transfer>> getAll(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        if (!currentUser.getRole().equals(Role.ADMIN)) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return new ResponseEntity<>(transfersRepo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Transfer>> getAllBySessionUserId(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @PathVariable("id") long id) {
        User user = usersRepo.findById(id);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (currentUser.getId() != id && currentUser.getRole() != Role.ADMIN)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<Transfer> transfers = transfersRepo.findAllBySessionUserIdOrReceiverUser_IdOrSenderUser_Id(id, id, id);

        for (Transfer transfer : transfers) {
            if (transfer.getSenderUser() != null && transfer.getSenderUser().getId() == id) {
                // Multiply transfer by -1 galaxy brain hack
                transfer.setAmount(transfer.getAmount().multiply(new BigDecimal(-1)));
            }
        }

        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/account/{iban}")
    public ResponseEntity<List<Transfer>> getAllByAccountIBAN(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @PathVariable("iban") String iban) {
        Account account = accountsRepo.findByIBAN(iban);
        if (account == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!Objects.equals(currentUser.getId(), account.getCustomer().getId()) && currentUser.getRole() != Role.ADMIN)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        List<Transfer> transfers = transfersRepo.findAllByAccountIBAN(iban);

        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Transfer> createTransfer(@Parameter(hidden = true) @AuthenticationPrincipal User currentUser, @RequestBody TransferManagers.CreateTransferForm form) {
        return transferManager.createTransfer(currentUser, form);
    }


}
