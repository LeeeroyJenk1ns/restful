package ru.murad.restful.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.murad.restful.dto.AccountToClientDTO;
import ru.murad.restful.dto.AccountToServerDTO;
import ru.murad.restful.dto.PinSumDTO;
import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.entities.Account;
import ru.murad.restful.entities.Transaction;
import ru.murad.restful.exceptions.InsufficientFundsException;
import ru.murad.restful.exceptions.InvalidPinException;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.repositories.AccountRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AccountServiceImplTest {

    @Autowired
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountRepository accountRepository;

    @Test
    public void shouldGetAccountById() {
        Account account = new Account();
        account.setName("Andrey");

        Transaction transaction1 = new Transaction(1, account, "deposit", 1000);
        Transaction transaction2 = new Transaction(2, account, "transfer", -500);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction1);
        transactions.add(transaction2);

        account.setTransactions(transactions);
        account.setBalance();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        AccountToClientDTO result = accountService.getAccountById(1L);

        assertEquals(500, account.getBalance());
        assertEquals("Andrey", result.getName());
        assertEquals(500, result.getBalance());
    }

    @Test
    public void shouldGetAccountByIdThrowsNotFoundException() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            accountService.getAccountById(1);
        });
    }

    @Test
    public void shouldCreateAccount() {
        AccountToServerDTO dto = new AccountToServerDTO();
        dto.setName("Andrey");
        dto.setPin(1111);

        when(accountRepository.save(any(Account.class))).thenReturn(new Account());

        AccountToClientDTO result = accountService.create(dto);

        assertEquals("Andrey", result.getName());
        assertEquals(0, result.getBalance());
    }

    @Test
    public void shouldGetAllAccounts() {
        Account account1 = new Account();
        account1.setName("Andrey1");

        Account account2 = new Account();
        account2.setName("Andrey2");

        List<Account> accounts = Arrays.asList(account1, account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        List<AccountToClientDTO> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals("Andrey1", result.get(0).getName());
        assertEquals(0, result.get(0).getBalance());
        assertEquals("Andrey2", result.get(1).getName());
        assertEquals(0, result.get(1).getBalance());
    }

    @Test
    public void shouldTransfer() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(1);
        fromAccount.setName("Andrey1");
        fromAccount.setPin(1111);

        Transaction transaction = new Transaction(1, fromAccount, "deposit", 1000);

        fromAccount.setTransactions(Collections.singletonList(transaction));
        fromAccount.setBalance();

        Account toAccount = new Account();
        toAccount.setAccountNumber(2);
        toAccount.setName("Andrey2");
        toAccount.setPin(2222);

        PinSumDTO dto = new PinSumDTO();
        dto.setPin(1111);
        dto.setSum(500);

        TransactionDTO transactionDTO = new TransactionDTO("transfer", 0 - dto.getSum(), new Date());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));
        when(transactionService.create(any(TransactionDTO.class), eq(fromAccount))).thenReturn(transactionDTO);

        TransactionDTO result = accountService.transfer(dto, 1, 2);

        fromAccount.setTransactions(Arrays.asList(transaction, new Transaction(2, fromAccount,
                result.getType(), result.getAmount())));

        toAccount.setTransactions(Collections.singletonList(new Transaction(3, toAccount, result.getType(),
                result.getAmount()*(-1))));

        fromAccount.setBalance();
        toAccount.setBalance();

        assertEquals("transfer", result.getType());
        assertEquals(-500, result.getAmount());

        assertEquals(500, fromAccount.getBalance());
        assertEquals(500, toAccount.getBalance());
    }

    @Test
    public void shouldTransferThrowsInvalidPinException() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(1);
        fromAccount.setName("Andrey1");
        fromAccount.setPin(1111);

        Account toAccount = new Account();
        toAccount.setAccountNumber(2);
        toAccount.setName("Andrey2");
        toAccount.setPin(2222);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        PinSumDTO dto = new PinSumDTO();
        dto.setPin(1112);
        dto.setSum(500);

        assertThrows(InvalidPinException.class, () -> {
            accountService.transfer(dto, 1, 2);
        });
    }

    @Test
    public void shouldTransferThrowsInsufficientFundsException() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber(1);
        fromAccount.setName("Andrey1");
        fromAccount.setPin(1111);

        Account toAccount = new Account();
        toAccount.setAccountNumber(2);
        toAccount.setName("Andrey2");
        toAccount.setPin(2222);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccount));

        PinSumDTO dto = new PinSumDTO();
        dto.setPin(1111);
        dto.setSum(500);

        assertThrows(InsufficientFundsException.class, () -> {
            accountService.transfer(dto, 1, 2);
        });
    }

    @Test
    public void shouldWithdraw() {
        Account account = new Account();
        account.setAccountNumber(1);
        account.setName("Andrey");
        account.setPin(1111);

        Transaction transaction = new Transaction(1, account, "deposit", 1000);

        account.setTransactions(Collections.singletonList(transaction));
        account.setBalance();

        PinSumDTO dto = new PinSumDTO();
        dto.setPin(1111);
        dto.setSum(500);

        TransactionDTO transactionDTO = new TransactionDTO("withdraw", 0 - dto.getSum(), new Date());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionService.create(any(TransactionDTO.class), eq(account))).thenReturn(transactionDTO);

        TransactionDTO result = accountService.withdraw(dto, 1L);

        account.setTransactions(Arrays.asList(transaction, new Transaction(2, account,
                result.getType(), result.getAmount())));

        account.setBalance();

        assertEquals("withdraw", result.getType());
        assertEquals(-500, result.getAmount());

        assertEquals(500, account.getBalance());
    }

    @Test
    public void shouldDeposit() {
        Account account = new Account();
        account.setAccountNumber(1);
        account.setName("Andrey");
        account.setPin(1111);

        PinSumDTO dto = new PinSumDTO();
        dto.setSum(4244);

        TransactionDTO transactionDTO = new TransactionDTO("deposit", dto.getSum(), new Date());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionService.create(any(TransactionDTO.class), eq(account))).thenReturn(transactionDTO);

        TransactionDTO result = accountService.deposit(dto, 1L);

        account.setTransactions(Collections.singletonList(new Transaction(1, account,
                result.getType(), result.getAmount())));
        account.setBalance();

        assertEquals("deposit", result.getType());
        assertEquals(4244, result.getAmount());

        assertEquals(4244, account.getBalance());
    }

}
