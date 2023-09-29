package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.SkuDto;
import ru.school21.retail.model.entity.Sku;
import ru.school21.retail.services.SkuService;


@Mapper(uses = { SkuGroupMapper.class } )
public abstract class SkuMapper extends GenericMapper<Sku, SkuDto> {
    @Autowired
    private SkuService skuService;

    public Long map(@NotNull Sku sku) {
        return sku.getId();
    }

    public Sku map(Long id) {
        return skuService.findById(id).orElseThrow();
    }
}
