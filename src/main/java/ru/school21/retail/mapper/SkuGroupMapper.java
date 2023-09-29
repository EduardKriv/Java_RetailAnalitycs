package ru.school21.retail.mapper;

import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.school21.retail.model.dto.SkuGroupDto;
import ru.school21.retail.model.entity.SkuGroup;
import ru.school21.retail.services.SkuGroupService;

@Mapper
public abstract class SkuGroupMapper extends GenericMapper<SkuGroup, SkuGroupDto> {
    @Autowired
    private SkuGroupService skuGroupService;

    public Long map(@NotNull SkuGroup skuGroup) {
        return skuGroup.getId();
    }

    public SkuGroup map(Long id) {
        return skuGroupService.findById(id).orElseThrow();
    }
}
