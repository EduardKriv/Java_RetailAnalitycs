package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "cards")
@EqualsAndHashCode(callSuper = false)
public class Card  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_card_id_generator")
    @SequenceGenerator(name = "customer_card_id_generator", sequenceName = "cards_customer_card_id_seq", allocationSize = 1)
    @Column(name = "customer_card_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Person person;
}
