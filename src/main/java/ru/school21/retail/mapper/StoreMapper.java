package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.StoreDto;
import ru.school21.retail.model.entity.Store;
import ru.school21.retail.services.StoreService;

@Mapper(uses = { TransactionMapper.class,  SkuMapper.class } )
public abstract class StoreMapper extends GenericMapper<Store, StoreDto> {
    @Autowired
    private StoreService storeService;

    public Long map(@NotNull Store store) {
        return store.getId();
    }

    public Store map(Long id) {
        return storeService.findById(id).orElseThrow();
    }
}
