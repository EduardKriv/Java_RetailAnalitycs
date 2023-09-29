package ru.school21.retail.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.CardMapper;
import ru.school21.retail.model.dto.CardDto;
import ru.school21.retail.model.entity.Card;
import ru.school21.retail.services.CardService;
import ru.school21.retail.services.PersonService;


@Controller
@RequestMapping("/cards")
public class CardController extends GenericController<Card, CardDto, Long> {
    private final PersonService personService;

    @Autowired
    public CardController(CardService genericService, CardMapper genericMapper, PersonService personService) {
        super(genericService, genericMapper, "cards", CardDto.class);
        this.personService = personService;
    }

    @Override
    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute("persons", personService.findAll());
        return super.findAll(model);
    }

    @Override
    @GetMapping("updates/{id}")
    public String update(@PathVariable Long id, Model model) {
        model.addAttribute("persons", personService.findAll());
        return super.update(id, model);
    }
}
