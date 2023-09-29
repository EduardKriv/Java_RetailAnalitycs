package ru.school21.retail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.school21.retail.model.entity.View;

@Repository
public interface ViewRepository extends JpaRepository<View, Long> {
}
