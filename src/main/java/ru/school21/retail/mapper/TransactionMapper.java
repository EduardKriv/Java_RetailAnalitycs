package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.TransactionDto;
import ru.school21.retail.model.entity.Transaction;
import ru.school21.retail.services.TransactionService;

@Mapper(uses = { CardMapper.class,  StoreMapper.class } )
public abstract class TransactionMapper extends GenericMapper<Transaction, TransactionDto> {
    @Autowired
    private TransactionService transactionService;

    public Long map(@NotNull Transaction transaction) {
        return transaction.getId();
    }

    public Transaction map(Long id) {
        return transactionService.findById(id).orElseThrow();
    }
}
