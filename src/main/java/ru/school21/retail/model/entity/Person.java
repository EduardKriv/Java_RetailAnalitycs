package ru.school21.retail.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Data
@Table(name = "personal_data")
@EqualsAndHashCode(callSuper = false)
public class Person  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_generator")
    @SequenceGenerator(name = "customer_id_generator", sequenceName = "personal_data_customer_id_seq", allocationSize = 1)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_surname", nullable = false)
    private String customerSurname;

    @Column(name = "customer_primary_email", nullable = false)
    private String customerPrimaryEmail;

    @Column(name = "customer_primary_phone", nullable = false)
    private String customerPrimaryPhone;
}
