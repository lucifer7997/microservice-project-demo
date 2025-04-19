package com.example.account_service.controller;

import com.example.account_service.dto.AccountDTO;
import com.example.account_service.entity.Account;
import com.example.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public List<AccountDTO> getAccounts() {
        List<Account> accounts = accountService.getListAccounts();
        return accounts.stream()
                .map(account -> new AccountDTO(account.getUsername(), account.getFirstName(), account.getLastName()))
                .toList();
    }
}
