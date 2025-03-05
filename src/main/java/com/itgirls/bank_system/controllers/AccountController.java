package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping
    public String getAllAccounts(Model model,
                                 @RequestParam(required = false) String accountNumber,
                                 @RequestParam(required = false) BigDecimal minBalance,
                                 @RequestParam(required = false) BigDecimal maxBalance) {
        try {
            log.info("Получение всех счетов с фильтрами...");
            List<AccountDto> accounts = accountService.getFilteredAccounts(accountNumber, minBalance, maxBalance);
            model.addAttribute("accounts", accounts);
            return "accounts";
        } catch (RuntimeException e) {
            log.error("Ошибка при получении счетов", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute AccountDto accountDto, Model model) {
        try {
            log.info("Создание нового счета...");
            CompletableFuture<AccountDto> createdAccount = accountService.createAccount(accountDto);
            model.addAttribute("account", createdAccount);
            return "redirect:/accounts";
        } catch (RuntimeException e) {
            log.error("Ошибка при создании счета", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String getAccountById(@PathVariable Long id, Model model) {
        try {
            log.info("Получение счета с ID: {}", id);
            AccountDto accountDto = accountService.getAccountById(id);
            model.addAttribute("account", accountDto);
            return "accountDetails";
        } catch (RuntimeException e) {
            log.error("Ошибка при получении счета с ID: {}", id, e);
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteAccount(@PathVariable Long id) {
        try {
            log.info("Удаление счета с ID: {}", id);
            accountService.deleteAccount(id);
            return "redirect:/accounts";
        } catch (RuntimeException e) {
            log.error("Ошибка при удалении счета с ID: {}", id, e);
            return "error";
        }
    }
}
