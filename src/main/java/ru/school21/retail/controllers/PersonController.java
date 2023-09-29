package ru.school21.retail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.PersonMapper;
import ru.school21.retail.model.dto.PersonDto;
import ru.school21.retail.model.entity.Person;
import ru.school21.retail.services.PersonService;


@Controller
@RequestMapping("/persons")
public class PersonController extends GenericController<Person, PersonDto, Long> {
    @Autowired
    public PersonController(PersonService personService, PersonMapper personMapper) {
        super(personService, personMapper, "persons", PersonDto.class);
    }
}
