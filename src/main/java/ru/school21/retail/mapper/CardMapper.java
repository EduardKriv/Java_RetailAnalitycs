package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.CardDto;
import ru.school21.retail.model.entity.Card;
import ru.school21.retail.services.CardService;


@Mapper(uses = { PersonMapper.class } )
public abstract class CardMapper extends GenericMapper<Card, CardDto> {
    @Autowired
    private CardService cardService;

    public Long map(@NotNull Card card) {
        return card.getId();
    }

    public Card map(Long id) {
        return cardService.findById(id).orElseThrow();
    }
}
