package ru.murad.restful.services.servicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.restful.dto.AccountToClientDTO;
import ru.murad.restful.dto.AccountToServerDTO;
import ru.murad.restful.dto.PinSumDTO;
import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.entities.Account;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.exceptions.InsufficientFundsException;
import ru.murad.restful.exceptions.InvalidPinException;
import ru.murad.restful.exceptions.TransferException;
import ru.murad.restful.repositories.AccountRepository;
import ru.murad.restful.services.AccountService;
import ru.murad.restful.services.TransactionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionService transactionService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    @Transactional(readOnly = true)
    @Override
    public AccountToClientDTO getAccountById(long id) {
        Optional<Account> account = accountRepository.findById(id);
        account.orElseThrow(NotFoundException::new);
        return new AccountToClientDTO(account.get().getName(), account.get().getBalance());
    }

    @Override
    public AccountToClientDTO create(AccountToServerDTO dto) {
        Account account = new Account();
        account.setName(dto.getName());
        account.setPin(dto.getPin());

        accountRepository.save(account);

        return new AccountToClientDTO(account.getName(), account.getBalance());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountToClientDTO> getAllAccounts() {
        List<AccountToClientDTO> accountDtoList = new ArrayList<>();
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            accountDtoList.add(new AccountToClientDTO(account.getName(), account.getBalance()));
        }

        return accountDtoList;
    }

    @Override
    public TransactionDTO transfer(PinSumDTO dto, long fromId, long toId) {
        if (fromId == toId) throw new TransferException();
        Account fromAccount = accountRepository.findById(fromId).orElseThrow(NotFoundException::new);
        Account toAccount = accountRepository.findById(toId).orElseThrow(NotFoundException::new);

        if (!checkPin(dto.getPin(), fromAccount.getPin())) {
            throw new InvalidPinException();
        }

        if (dto.getSum() > fromAccount.getBalance()) {
            throw new InsufficientFundsException();
        }

        transactionService.create(new TransactionDTO("transfer", dto.getSum(), new Date()), toAccount);
        return transactionService.create(new TransactionDTO("transfer", 0 - dto.getSum(), new Date()), fromAccount);
    }

    @Override
    public TransactionDTO withdraw(PinSumDTO dto, long id) {
        Account account = accountRepository.findById(id).orElseThrow(NotFoundException::new);

        if (!checkPin(dto.getPin(), account.getPin())) {
            throw new InvalidPinException();
        }

        if (dto.getSum() > account.getBalance()) {
            throw new InsufficientFundsException();
        }

        return transactionService.create(new TransactionDTO("withdraw", 0 - dto.getSum(), new Date()), account);
    }

    @Override
    public TransactionDTO deposit(PinSumDTO dto, long id) {
        Account account = accountRepository.findById(id).orElseThrow(NotFoundException::new);

        return transactionService.create(new TransactionDTO("deposit", dto.getSum(), new Date()), account);
    }

    private boolean checkPin(int firstPin, int secondPin) {
        return firstPin == secondPin;
    }
}
