package ru.murad.restful.services;

import ru.murad.restful.dto.AccountToClientDTO;
import ru.murad.restful.dto.AccountToServerDTO;
import ru.murad.restful.dto.PinSumDTO;
import ru.murad.restful.dto.TransactionDTO;

import java.util.List;

public interface AccountService {
    AccountToClientDTO getAccountById(long id);
    AccountToClientDTO create(AccountToServerDTO dto);
    List<AccountToClientDTO > getAllAccounts();
    TransactionDTO transfer(PinSumDTO dto, long fromId, long toId);
    TransactionDTO withdraw(PinSumDTO dto, long id);
    TransactionDTO deposit(PinSumDTO dto, long id);
}
