package com.ahmed.bank_api.controller;
import com.ahmed.bank_api.dto.AmountRequest;
import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.dto.UpdateAccountRequest;
import com.ahmed.bank_api.model.Account;
import jakarta.validation.Valid;
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

    @GetMapping("/accounts/{id}")
    public Account getOneAccount(@PathVariable Long id){
        return accountService.find(id);
    }

    @PostMapping("/accounts")
    public Account create(@Valid @RequestBody CreateAccountRequest request){

       return accountService.createAccount(request);


    }
    @PostMapping("/accounts/{id}/deposit")
    public Account deposit(@PathVariable Long id , @RequestBody AmountRequest request){
        return accountService.deposit(id , request.getAmount());
    }
    @PostMapping("/accounts/{id}/withdraw")
    public Account withdraw(@PathVariable Long id , @RequestBody AmountRequest request){
        return accountService.withdraw(id , request.getAmount());
    }

//    @PutMapping("/accounts/{id}")
//    public Account update(@PathVariable Long id , @Valid @RequestBody UpdateAccountRequest request){
//        Account account = new Account();
//
//        return accountService.update(id , account);
//    }

    @DeleteMapping("/accounts/{id}")
    public Account delete(@PathVariable Long id){
        return accountService.delete(id);
    }

    @PatchMapping("/accounts/{id}/block")
    public Account block(@PathVariable Long id){
        return accountService.blockAccount(id);
    }

    @PatchMapping("/accounts/{id}/activate")
    public Account activate(@PathVariable Long id){
        return accountService.activateAccount(id);
    }

    @PatchMapping("/accounts/{id}/close")
    public Account close(@PathVariable Long id){
        return accountService.closeAccount(id);
    }











}
