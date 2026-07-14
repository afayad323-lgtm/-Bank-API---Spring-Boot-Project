package com.ahmed.bank_api.controller;
import com.ahmed.bank_api.dto.AmountRequest;
import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.dto.TransferRequest;
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

    @GetMapping("/accounts/{accountNumber}")
    public Account getOneAccount(@PathVariable String accountNumber){
        return accountService.findByAccountNumber(accountNumber);
    }

    @PostMapping("/accounts")
    public Account create(@Valid @RequestBody CreateAccountRequest request){

       return accountService.createAccount(request);


    }
    @PostMapping("/accounts/{accountNumber}/deposit")
    public Account deposit(@PathVariable String accountNumber , @RequestBody AmountRequest request){
        return accountService.deposit(accountNumber , request.getAmount());
    }
    @PostMapping("/accounts/{accountNumber}/withdraw")
    public Account withdraw(@PathVariable String accountNumber, @RequestBody AmountRequest request){
        return accountService.withdraw(accountNumber , request.getAmount());
    }

//    @PutMapping("/accounts/{id}")
//    public Account update(@PathVariable Long id , @Valid @RequestBody UpdateAccountRequest request){
//        Account account = new Account();
//
//        return accountService.update(id , account);
//    }

    @DeleteMapping("/accounts/{accountNumber}")
    public Account delete(@PathVariable String accountNumber){
        return accountService.delete(accountNumber);
    }

    @PatchMapping("/accounts/{accountNumber}/block")
    public Account block(@PathVariable String accountNumber){
        return accountService.blockAccount(accountNumber);
    }

    @PatchMapping("/accounts/{accountNumber}/activate")
    public Account activate(@PathVariable String accountNumber){
        return accountService.activateAccount(accountNumber);
    }

    @PatchMapping("/accounts/{accountNumber}/close")
    public Account close(@PathVariable String accountNumber){
        return accountService.closeAccount(accountNumber);
    }

@PostMapping("/accounts/transfer")
    public Account transfer(@RequestBody TransferRequest request){
        return accountService.transfer(request);
}









}
