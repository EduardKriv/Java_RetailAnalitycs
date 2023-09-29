package ru.school21.retail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school21.retail.model.entity.SkuGroup;
import ru.school21.retail.repositories.SkuGroupRepository;

@Service
public class SkuGroupService extends BaseService<SkuGroup, Long> {

    @Autowired
    protected SkuGroupService(SkuGroupRepository repository) {
        super(repository);
    }
}
