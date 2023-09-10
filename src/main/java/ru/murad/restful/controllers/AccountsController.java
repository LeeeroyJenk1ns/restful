package ru.murad.restful.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.murad.restful.dto.AccountToClientDTO;
import ru.murad.restful.dto.AccountToServerDTO;
import ru.murad.restful.dto.PinSumDTO;
import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.exceptions.InsufficientFundsException;
import ru.murad.restful.exceptions.InvalidPinException;
import ru.murad.restful.exceptions.TransferException;
import ru.murad.restful.services.AccountService;
import ru.murad.restful.utils.ErrorResponse;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountsController {
    private final AccountService accountService;

    @Autowired
    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountToClientDTO>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AccountToClientDTO> create(@RequestBody AccountToServerDTO dto) {
        return new ResponseEntity<>(accountService.create(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountToClientDTO> getAccount(@PathVariable("id") long id) {
        return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
    }

    @PostMapping("/{fromId}/transfer/{toId}")
    public ResponseEntity<TransactionDTO> transfer(@PathVariable("fromId") long fromId,
                                                   @PathVariable("toId") long toId,
                                                   @RequestBody PinSumDTO dto) {
        return new ResponseEntity<>(accountService.transfer(dto, fromId, toId), HttpStatus.OK);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@PathVariable("id") long id, @RequestBody PinSumDTO dto) {
        return new ResponseEntity<>(accountService.withdraw(dto, id), HttpStatus.OK);
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TransactionDTO> deposit(@PathVariable("id") long id, @RequestBody PinSumDTO dto) {
        return new ResponseEntity<>(accountService.deposit(dto, id), HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Account not found.",
                new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPinException.class)
    private ResponseEntity<ErrorResponse> handleInvalidPinException(InvalidPinException e) {
        ErrorResponse response = new ErrorResponse(
                "Invalid PIN code",
                new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    private ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException e) {
        ErrorResponse response = new ErrorResponse(
                "insufficient funds",
                new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TransferException.class)
    private ResponseEntity<ErrorResponse> handleTransferException(TransferException e) {
        ErrorResponse response = new ErrorResponse(
                "translation to yourself",
                new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
