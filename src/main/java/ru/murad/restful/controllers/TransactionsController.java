package ru.murad.restful.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.services.TransactionService;
import ru.murad.restful.utils.ErrorResponse;

import java.util.Date;
import java.util.List;

@RestController
public class TransactionsController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable("id") long id) {
        return new ResponseEntity<>(transactionService.getTransactionById(id), HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/transactions")
    public ResponseEntity<List<TransactionDTO>> getAllTransactionByAccount(@PathVariable("id") long id) {
        return new ResponseEntity<>(transactionService.getAllTransactionByAccountId(id), HttpStatus.OK);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Transaction not found.",
                new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
