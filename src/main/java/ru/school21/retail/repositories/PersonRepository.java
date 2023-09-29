package ru.school21.retail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.school21.retail.model.entity.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
