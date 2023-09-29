package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Transaction;
import ru.school21.retail.repositories.TransactionRepository;

@Service
public class TransactionService extends BaseService<Transaction, Long> {

    @Autowired
    public TransactionService(TransactionRepository repository) {
        super(repository);
    }
}
