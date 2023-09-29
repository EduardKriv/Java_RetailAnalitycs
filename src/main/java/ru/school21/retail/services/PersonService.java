package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Person;
import ru.school21.retail.repositories.PersonRepository;

@Service
public class PersonService extends BaseService<Person, Long> {

    @Autowired
    protected PersonService(PersonRepository repository) {
        super(repository);
    }
}
