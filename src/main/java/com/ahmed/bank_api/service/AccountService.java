package com.ahmed.bank_api.service;
import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.exception.AccountNotFound;
import com.ahmed.bank_api.exception.InSufficientAmount;
import com.ahmed.bank_api.exception.InValidAmount;
import com.ahmed.bank_api.model.Account;
import java.util.List;
import java.util.ArrayList;

import com.ahmed.bank_api.model.AccountStatus;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.repository.AccountRepository;
import com.ahmed.bank_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;

  public AccountService(AccountRepository accountRepository , CustomerRepository customerRepository){
      this.accountRepository =accountRepository;
      this.customerRepository = customerRepository;
  }

    public List<Account> getAccounts(){
        return accountRepository.findAll();
    }

  public Account createAccount(CreateAccountRequest request){
      Customer customer = customerRepository.findById(request.getCustomerId())
              .orElseThrow(()-> new RuntimeException("Customer Not Found"));
      Account account = new Account();
      account.setBalance(0);
      account.setCustomer(customer);
      account.setAccountType(request.getAccountType());
      if(accountRepository.existsByCustomerIdAndAccountType(
              customer.getId(),
              request.getAccountType())){

          throw new RuntimeException("Customer already has this account type");
      }

      account.setAccountStatus(AccountStatus.ACTIVE);
      accountRepository.save(account);

      account.setAccountNumber("ACC" + String.format("%06d",account.getId()));
      return accountRepository.save(account);

  }

    public Account findByAccountNumber(String accountNumber){
        return accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(()-> new AccountNotFound("Account Not Found: " + accountNumber));
    }

    public Account deposit(String accountNumber , double amount){
      if (amount <= 0){
          throw new InValidAmount("Invalid amount");

      }
      Account acc = findByAccountNumber(accountNumber);
      if (acc.getAccountStatus() != AccountStatus.ACTIVE){
          throw new RuntimeException("Account Is Not Active");
      }

      acc.setBalance(acc.getBalance() + amount);

      return accountRepository.save(acc);
    }

    public Account withdraw(String accountNumber , double amount){
        if (amount <= 0){
            throw new InValidAmount("Invalid amount");

        }


        Account acc = findByAccountNumber(accountNumber);
        if (acc.getAccountStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account Is Not Active");
        }

        if (amount > acc.getBalance()){
            throw new InSufficientAmount("Insufficient balance");
        }
        acc.setBalance(acc.getBalance() - amount);

        return accountRepository.save(acc);

    }

//    public Account update(Long id , Account updatedAccount){
//        Account oldAccount = find(id);
//oldAccount.setCustomer();
//
//
//        return accountRepository.save(oldAccount);
//    }
    public Account delete(String accountNumber ){
        Account acc = findByAccountNumber(accountNumber);
        accountRepository.delete(acc);
        return acc;


    }

    public Account blockAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Closed Account Cannot Be Blocked");
      }
      if (account.getAccountStatus() == AccountStatus.BLOCKED){
          throw new RuntimeException("Account Already Blocked");
      }
      account.setAccountStatus(AccountStatus.BLOCKED);
      return accountRepository.save(account);
    }
    public Account activateAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Closed Account Cannot Be Activated");
      }
      if (account.getAccountStatus() == AccountStatus.ACTIVE){
          throw new RuntimeException("Account Already Activated");
      }
      account.setAccountStatus(AccountStatus.ACTIVE);
      return accountRepository.save(account);
    }

    public Account closeAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Account Already Closed");
      }
      account.setAccountStatus(AccountStatus.CLOSED);
      return accountRepository.save(account);
    }

}
