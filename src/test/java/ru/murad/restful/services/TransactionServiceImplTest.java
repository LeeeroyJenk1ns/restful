package ru.murad.restful.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.murad.restful.dto.TransactionDTO;

import static org.junit.jupiter.api.Assertions.*;

import ru.murad.restful.entities.Account;
import ru.murad.restful.entities.Transaction;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.repositories.TransactionRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TransactionServiceImplTest {

    @Autowired
    private TransactionService transactionService;

    @MockBean
    private TransactionRepository transactionRepository;

    @Test
    public void shouldGetTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setType("transfer");
        transaction.setAmount(100);
        transaction.setCreatedAt(new Date());

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.getTransactionById(1L);

        assertNotNull(result);
        assertEquals("transfer", result.getType());
        assertEquals(100, result.getAmount());
        assertEquals(transaction.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    public void shouldGetTransactionByIdThrowsNotFoundException() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            transactionService.getTransactionById(1L);
        });
    }

    @Test
    public void shouldGetAllTransactionByAccountId() {
        Account account = new Account();
        account.setAccountNumber(1234567890L);

        Transaction transaction1 = new Transaction(1, account, "transfer", 100);
        transaction1.setCreatedAt(new Date());

        Transaction transaction2 = new Transaction(2, account, "deposit", 50);
        transaction2.setCreatedAt(new Date());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        when(transactionRepository.findAllByAccount_AccountNumber(anyLong())).thenReturn(transactions);

        List<TransactionDTO> result = transactionService.getAllTransactionByAccountId(1234567890L);

        assertEquals(2, result.size());
        assertEquals("transfer", result.get(0).getType());
        assertEquals(100, result.get(0).getAmount());
        assertEquals(transaction1.getCreatedAt(), result.get(0).getCreatedAt());
        assertEquals("deposit", result.get(1).getType());
        assertEquals(50, result.get(1).getAmount());
        assertEquals(transaction2.getCreatedAt(), result.get(1).getCreatedAt());
    }

    @Test
    public void shouldCreateTransaction() {
        Account account = new Account();
        account.setAccountNumber(1234);

        TransactionDTO dto = new TransactionDTO("transfer", 100, new Date());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        TransactionDTO result = transactionService.create(dto, account);

        assertEquals("transfer", result.getType());
        assertEquals(100, result.getAmount());
        assertEquals(dto.getCreatedAt(), result.getCreatedAt());
    }
}
