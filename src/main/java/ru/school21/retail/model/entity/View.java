package ru.school21.retail.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "views")
@EqualsAndHashCode(callSuper = false)
public class View extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "view")
    private String query;
}
