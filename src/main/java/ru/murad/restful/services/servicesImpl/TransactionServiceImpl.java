package ru.murad.restful.services.servicesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.murad.restful.dto.TransactionDTO;
import ru.murad.restful.entities.Account;
import ru.murad.restful.entities.Transaction;
import ru.murad.restful.exceptions.NotFoundException;
import ru.murad.restful.repositories.TransactionRepository;
import ru.murad.restful.services.TransactionService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionDTO getTransactionById(long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        transaction.orElseThrow(NotFoundException::new);
        return new TransactionDTO(transaction.get().getType(),
                transaction.get().getAmount(), transaction.get().getCreatedAt());
    }

    @Override
    public List<TransactionDTO> getAllTransactionByAccountId(long id) {
        List<Transaction> transactions = transactionRepository.findAllByAccount_AccountNumber(id);
        List<TransactionDTO> transactionDTOList = new ArrayList<>();

        for(Transaction transaction : transactions) {
            transactionDTOList.add(new TransactionDTO(transaction.getType(),
                    transaction.getAmount(), transaction.getCreatedAt()));
        }
        return transactionDTOList;
    }

    @Transactional(readOnly = false)
    @Override
    public TransactionDTO create(TransactionDTO dto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setAccount(account);
        transaction.setCreatedAt(dto.getCreatedAt());

        transactionRepository.save(transaction);

        return new TransactionDTO(transaction.getType(), transaction.getAmount(), transaction.getCreatedAt());
    }
}
