package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Data
@Table(name = "stores")
@EqualsAndHashCode(callSuper = false)
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_id_generator")
    @SequenceGenerator(name = "store_id_generator", sequenceName = "stores_store_id_seq", allocationSize = 1)
    @Column(name = "store_id")
    private Long id;

    @Column(name = "transaction_store_id", nullable = false)
    private Long transactionStore;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_id", referencedColumnName = "sku_id", nullable = false)
    private Sku sku;

    @Column(name = "sku_purchase_price", nullable = false)
    private Double skuPurchasePrice;

    @Column(name = "sku_retail_price", nullable = false)
    private Double skuRetailPrice;
}
