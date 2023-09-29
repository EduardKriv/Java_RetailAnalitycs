package ru.school21.retail.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.CheckMapper;
import ru.school21.retail.mapper.SkuMapper;
import ru.school21.retail.model.dto.CheckDto;
import ru.school21.retail.model.entity.Check;
import ru.school21.retail.services.CheckService;
import ru.school21.retail.services.SkuService;
import ru.school21.retail.services.TransactionService;

@Controller
@RequestMapping("/checks")
public class CheckController extends GenericController<Check, CheckDto, Long> {
    private final TransactionService transactionService;
    private final SkuService skuService;

    @Autowired
    public CheckController(CheckService checkService, CheckMapper checkMapper, SkuService skuService,
                           TransactionService transactionService, SkuMapper skuMapper) {
        super(checkService, checkMapper, "checks", CheckDto.class);
        this.skuService = skuService;
        this.transactionService = transactionService;
    }

    @Override
    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute("skus", skuService.findAll());
        model.addAttribute("transactions", transactionService.findAll());
        return super.findAll(model);
    }

    @Override
    @GetMapping("updates/{id}")
    public String update(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("skus", skuService.findAll());
        model.addAttribute("transactions", transactionService.findAll());
        return super.update(id,model);
    }
}
