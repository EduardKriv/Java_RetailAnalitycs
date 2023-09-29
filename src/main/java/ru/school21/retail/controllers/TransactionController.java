package ru.school21.retail.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.school21.retail.mapper.TransactionMapper;
import ru.school21.retail.model.dto.TransactionDto;
import ru.school21.retail.model.entity.Store;
import ru.school21.retail.model.entity.Transaction;
import ru.school21.retail.services.CardService;
import ru.school21.retail.services.StoreService;
import ru.school21.retail.services.TransactionService;

@Controller
@RequestMapping("/transactions")
public class TransactionController extends GenericController<Transaction, TransactionDto, Long> {
    private final CardService cardService;
    private final StoreService storeService;
    @Autowired
    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper,
                                 CardService cardService, StoreService storeService) {
        super(transactionService, transactionMapper, "transactions", TransactionDto.class);
        this.storeService = storeService;
        this.cardService = cardService;
    }

    @Override
    @GetMapping("all")
    public String findAll(@NotNull Model model) {
        model.addAttribute("cards", cardService.findAll());
        model.addAttribute("stores", storeService.findAll().stream()
                                                                .map(Store::getTransactionStore)
                                                                .distinct()
                                                                .toList());
        return super.findAll(model);
    }

    @Override
    @GetMapping("updates/{id}")
    public String update(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("stores", storeService.findAll().stream().map(Store::getTransactionStore).distinct().toList());
        model.addAttribute("cards", cardService.findAll());
        return super.update(id,model);
    }
}
