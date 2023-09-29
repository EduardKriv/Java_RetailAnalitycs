package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
@EqualsAndHashCode(callSuper = false)
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    @SequenceGenerator(name = "transaction_id_generator", sequenceName = "transactions_transaction_id_seq", allocationSize = 1)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_card_id", referencedColumnName = "customer_card_id", nullable = false)
    private Card customerCard;

    @Column(name = "transaction_summ", nullable = false)
    private Double transactionSumm;

    @Column(name = "transaction_datetime", nullable = false)
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime transactionDateTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_store_id", referencedColumnName = "store_id", nullable = false)
    private Store transactionStore;
}
