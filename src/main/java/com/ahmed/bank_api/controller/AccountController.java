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
    public Account addAccount(@RequestBody Account account){
       return accountService.addAccount(account);

    }
    @PostMapping("/accounts/{name}/deposit")
    public Account deposit(@PathVariable String name , @RequestParam double amount){
        return accountService.deposit(name , amount);
    }
    @PostMapping("/accounts/{name}/withdraw")
    public Account withdraw(@PathVariable String name , @RequestParam double amount){
        return accountService.withdraw(name , amount);
    }

    @PutMapping("/accounts/{name}")
    public Account update(@PathVariable String name , @RequestBody Account account){
        return accountService.update(name , account);
    }

    @DeleteMapping("/accounts/{name}")
    public Account delete(@PathVariable String name){
        return accountService.delete(name);
    }











}
