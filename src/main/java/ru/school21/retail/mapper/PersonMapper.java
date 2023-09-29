package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.PersonDto;
import ru.school21.retail.model.entity.Person;
import ru.school21.retail.services.PersonService;

@Mapper
public abstract class PersonMapper extends GenericMapper<Person, PersonDto> {
    @Autowired
    private PersonService personService;

    public Long map(@NotNull Person person) {
        return person.getId();
    }

    public Person map(Long id) {
        return personService.findById(id).orElseThrow();
    }
}
