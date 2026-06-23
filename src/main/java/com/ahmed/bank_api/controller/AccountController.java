package com.ahmed.bank_api.controller;
import com.ahmed.bank_api.model.Account;
import org.springframework.web.bind.annotation.*;

import com.ahmed.bank_api.service.AccountService;

import java.util.List;

@RestController
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService){
        this.accountService = accountService;
    }
    @GetMapping("/accounts")
    public List<Account> getAllAccounts(){
        return accountService.getAccounts();
    }

    @GetMapping("/accounts/{name}")
    public Account getOneAccount(@PathVariable String name){
        return accountService.find(name);
    }

    @PostMapping("/accounts")
    public String addAccount(@RequestBody Account account){
        accountService.addAccount(account);
        return "Account added successfully";
    }
    @PostMapping("/{name}/deposit")
    public Account deposit(@PathVariable String name , @RequestParam double amount){
        return accountService.deposit(name , amount);
    }
    @PostMapping("/{name}/withdraw")
    public Account withdraw(@PathVariable String name , @RequestParam double amount){
        return accountService.withdraw(name , amount);
    }











}
