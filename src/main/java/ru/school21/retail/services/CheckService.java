package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Check;
import ru.school21.retail.repositories.CheckRepository;

@Service
public class CheckService extends BaseService<Check, Long> {

    @Autowired
    protected CheckService(CheckRepository repository) {
        super(repository);
    }
}
