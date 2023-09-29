package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Card;
import ru.school21.retail.repositories.CardRepository;

@Service
public class CardService extends BaseService<Card, Long> {

    @Autowired
    protected CardService(CardRepository repository) {
        super(repository);
    }
}
