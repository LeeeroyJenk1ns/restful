package ru.murad.restful.services;

import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.entities.Account;

import java.util.List;

public interface TransactionService {
    TransactionDTO getTransactionById(long id);
    List<TransactionDTO> getAllTransactionByAccountId(long id);
    TransactionDTO create(TransactionDTO dto, Account account);
}
