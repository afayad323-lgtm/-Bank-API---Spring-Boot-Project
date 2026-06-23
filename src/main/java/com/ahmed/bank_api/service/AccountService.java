package com.ahmed.bank_api.service;
import com.ahmed.bank_api.exception.AccountNotFound;
import com.ahmed.bank_api.exception.InSufficientAmount;
import com.ahmed.bank_api.exception.InValidAmount;
import com.ahmed.bank_api.model.Account;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private List<Account> accounts = new ArrayList<>();

    public List<Account> getAccounts(){
        return accounts;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public Account find(String name){
        return accounts.stream()
                .filter(n -> n.getOwnerName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(()-> new AccountNotFound("Account Not Found: " + name));
    }

    public Account deposit(String name , double amount){
      if (amount <= 0){
          throw new InValidAmount("Invalid amount");

      }
      Account acc = find(name);

      acc.setBalance(acc.getBalance() + amount);
      return acc;
    }

    public Account withdraw(String name , double amount){
        if (amount <= 0){
            throw new InValidAmount("Invalid amount");

        }


        Account acc = find(name);


        if (amount > acc.getBalance()){
            throw new InSufficientAmount("Insufficient balance");
        }
        acc.setBalance(acc.getBalance() - amount);
        return acc;

    }
}
