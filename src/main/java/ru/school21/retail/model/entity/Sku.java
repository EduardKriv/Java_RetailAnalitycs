package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "sku")
@EqualsAndHashCode(callSuper = false)
public class Sku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sku_id_generator")
    @SequenceGenerator(name = "sku_id_generator", sequenceName = "sku_sku_id_seq", allocationSize = 1)
    @Column(name = "sku_id")
    private Long id;

    @Column(name = "sku_name", nullable = false)
    private String skuName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false)
    private SkuGroup skuGroup;
}
