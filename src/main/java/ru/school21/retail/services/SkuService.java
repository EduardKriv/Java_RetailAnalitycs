package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.Sku;
import ru.school21.retail.repositories.SkuRepository;

@Service
public class SkuService extends BaseService<Sku, Long> {

    @Autowired
    protected SkuService(SkuRepository repository) {
        super(repository);
    }
}
