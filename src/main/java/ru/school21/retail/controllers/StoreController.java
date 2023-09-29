package ru.school21.retail.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.StoreMapper;
import ru.school21.retail.model.dto.StoreDto;
import ru.school21.retail.model.entity.Store;
import ru.school21.retail.services.SkuService;
import ru.school21.retail.services.StoreService;

@Controller
@RequestMapping("/stores")
public class StoreController extends GenericController<Store, StoreDto, Long> {
    private final SkuService skuService;

    @Autowired
    public StoreController(StoreService storeService, StoreMapper storeMapper, SkuService skuService) {
        super(storeService, storeMapper, "stores", StoreDto.class);
        this.skuService = skuService;
    }

    @Override
    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute("skus", skuService.findAll());
        model.addAttribute("transactionStore", genericService.findAll().stream()
                                                                        .map(Store::getTransactionStore)
                                                                        .distinct()
                                                                        .toList());
        return super.findAll(model);
    }

    @Override
    @GetMapping("updates/{id}")
    public String update(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("skus", skuService.findAll());
        model.addAttribute("transactionStore", genericService.findAll().stream()
                                                                        .map(Store::getTransactionStore)
                                                                        .distinct()
                                                                        .toList());
        return super.update(id,model);
    }
}
