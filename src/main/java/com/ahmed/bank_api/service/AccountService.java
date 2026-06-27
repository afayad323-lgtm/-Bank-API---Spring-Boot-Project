package com.ahmed.bank_api.service;
import com.ahmed.bank_api.exception.AccountNotFound;
import com.ahmed.bank_api.exception.InSufficientAmount;
import com.ahmed.bank_api.exception.InValidAmount;
import com.ahmed.bank_api.model.Account;
import java.util.List;
import java.util.ArrayList;

import com.ahmed.bank_api.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository){
      this.accountRepository =accountRepository;
  }

    public List<Account> getAccounts(){
        return accountRepository.findAll();
    }

    public Account addAccount(Account account){
         return accountRepository.save(account);


    }

    public Account find(Long id){
        return accountRepository
                .findById(id)
                .orElseThrow(()-> new AccountNotFound("Account Not Found: " + id));
    }

    public Account deposit(Long id , double amount){
      if (amount <= 0){
          throw new InValidAmount("Invalid amount");

      }
      Account acc = find(id);

      acc.setBalance(acc.getBalance() + amount);

      return accountRepository.save(acc);
    }

    public Account withdraw(Long id , double amount){
        if (amount <= 0){
            throw new InValidAmount("Invalid amount");

        }


        Account acc = find(id);


        if (amount > acc.getBalance()){
            throw new InSufficientAmount("Insufficient balance");
        }
        acc.setBalance(acc.getBalance() - amount);

        return accountRepository.save(acc);

    }

    public Account update(Long id , Account updatedAccount){
        Account oldAccount = find(id);

        oldAccount.setOwnerName(updatedAccount.getOwnerName());

        return accountRepository.save(oldAccount);
    }
    public Account delete(Long id ){
        Account acc = find(id);
        accountRepository.delete(acc);
        return acc;


    }
}
