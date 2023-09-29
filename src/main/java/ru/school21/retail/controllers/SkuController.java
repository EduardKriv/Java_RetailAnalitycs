package ru.school21.retail.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.SkuMapper;
import ru.school21.retail.model.dto.SkuDto;
import ru.school21.retail.model.entity.Sku;
import ru.school21.retail.services.SkuGroupService;
import ru.school21.retail.services.SkuService;

@Controller
@RequestMapping("/skus")
public class SkuController extends GenericController<Sku, SkuDto, Long> {
    private final SkuGroupService skuGroupService;

    @Autowired
    public SkuController(SkuService genericService, SkuMapper genericMapper, SkuGroupService skuGroupService) {
        super(genericService, genericMapper, "skus", SkuDto.class);
        this.skuGroupService = skuGroupService;
    }

    @Override
    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute("groups", skuGroupService.findAll());
        return super.findAll(model);
    }

    @Override
    @GetMapping("updates/{id}")
    public String update(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("groups", skuGroupService.findAll());
        return super.update(id, model);
    }
}
