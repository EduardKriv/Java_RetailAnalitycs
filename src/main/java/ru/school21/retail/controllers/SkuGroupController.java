package ru.school21.retail.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.SkuGroupMapper;
import ru.school21.retail.model.dto.SkuGroupDto;
import ru.school21.retail.model.entity.SkuGroup;
import ru.school21.retail.services.SkuGroupService;

@Controller
@RequestMapping("/skugroups")
public class SkuGroupController extends GenericController<SkuGroup, SkuGroupDto, Long> {
    @Autowired
    public SkuGroupController(SkuGroupService skuGroupService, SkuGroupMapper skuGroupMapper) {
        super(skuGroupService, skuGroupMapper, "skugroups", SkuGroupDto.class);
    }
}
