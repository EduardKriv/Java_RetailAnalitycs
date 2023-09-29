package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Store;
import ru.school21.retail.repositories.StoreRepository;

@Service
public class StoreService extends BaseService<Store, Long> {

    @Autowired
    public StoreService(StoreRepository repository) {
        super(repository);
    }
}
