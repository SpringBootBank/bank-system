package com.itgirls.bank_system.controllers;

import com.itgirls.bank_system.dto.AccountDto;
import com.itgirls.bank_system.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/accounts")
@AllArgsConstructor
@Slf4j
@Tag(name = "Счета", description = "Управление счетами пользователей")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public String getAllAccounts(Model model,
                                 @RequestParam(required = false) String accountNumber,
                                 @RequestParam(required = false) BigDecimal minBalance,
                                 @RequestParam(required = false) BigDecimal maxBalance) {
        log.info("Запрос на получение счетов");
        List<AccountDto> accounts = accountService.getFilteredAccounts(accountNumber, minBalance, maxBalance);
        model.addAttribute("accounts", accounts);
        return "accounts";
    }

    @GetMapping("/create")
    public String createAccountForm(Model model) {
        model.addAttribute("accountDto", new AccountDto());
        return "createAccount";
    }

    @PostMapping("/create")
    public String createAccount(@Valid @ModelAttribute AccountDto accountDto, Model model) {
        log.info("Создание счета: {}", accountDto);
        AccountDto createdAccount = accountService.createAccount(accountDto).join();
        model.addAttribute("account", createdAccount);
        return "redirect:/accounts";
    }

    @GetMapping("/{id}")
    public String getAccountById(@PathVariable Long id, Model model) {
        log.info("Запрос на получение счета с ID: {}", id);
        AccountDto accountDto = accountService.getAccountById(id);
        model.addAttribute("account", accountDto);
        return "accountDetails";
    }

    @GetMapping("/{id}/edit")
    public String editAccountForm(@PathVariable Long id, Model model) {
        log.info("Редактирование счета с ID: {}", id);
        AccountDto accountDto = accountService.getAccountById(id);
        model.addAttribute("accountDto", accountDto);
        return "editAccount";
    }

    @PostMapping("/{id}/edit")
    public String updateAccount(@PathVariable Long id, @Valid @ModelAttribute AccountDto accountDto, Model model) {
        log.info("Обновление счета с ID: {}", id);
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto).join();
        model.addAttribute("account", updatedAccount);
        return "redirect:/accounts";
    }

    @PostMapping("/{id}/delete")
    public String deleteAccount(@PathVariable Long id) {
        log.info("Удаление счета с ID: {}", id);
        accountService.deleteAccount(id);
        return "redirect:/accounts";
    }
}
