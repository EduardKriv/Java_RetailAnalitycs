package ru.school21.retail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.school21.retail.model.entity.Transaction;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
}
