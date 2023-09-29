package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "checks")
@EqualsAndHashCode(callSuper = false)
public class Check extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checks_id_generator")
    @SequenceGenerator(name = "checks_id_generator", sequenceName = "checks_checks_id_seq", allocationSize = 1)
    @Column(name = "checks_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_id", nullable = false)
    private Sku sku;

    @Column(name = "sku_amount", nullable = false)
    private Double skuAmount;

    @Column(name = "sku_summ", nullable = false)
    private Double skuSumm;

    @Column(name = "sku_summ_paid", nullable = false)
    private Double skuSummPaid;

    @Column(name = "sku_discount", nullable = false)
    private Double skuDiscount;
}
