package com.ahmed.bank_api.service;
import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.dto.TransferRequest;
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
import jakarta.transaction.Transactional;
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

    @Transactional
    public Account deposit(String accountNumber , double amount){
      if (amount <= 0){
          throw new InValidAmount("Invalid amount");

      }
      Account acc = findByAccountNumber(accountNumber);
      if (acc.getAccountStatus() != AccountStatus.ACTIVE){
          throw new RuntimeException("Account Is Not Active");
      }

      acc.setBalance(acc.getBalance() + amount);

      return acc;
    }

    @Transactional
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

        return acc;

    }


    public Account delete(String accountNumber ){
        Account acc = findByAccountNumber(accountNumber);
        accountRepository.delete(acc);
        return acc;


    }

    @Transactional
    public Account blockAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Closed Account Cannot Be Blocked");
      }
      if (account.getAccountStatus() == AccountStatus.BLOCKED){
          throw new RuntimeException("Account Already Blocked");
      }
      account.setAccountStatus(AccountStatus.BLOCKED);
      return account;
    }

    @Transactional
    public Account activateAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Closed Account Cannot Be Activated");
      }
      if (account.getAccountStatus() == AccountStatus.ACTIVE){
          throw new RuntimeException("Account Already Activated");
      }
      account.setAccountStatus(AccountStatus.ACTIVE);
      return account;
    }

    @Transactional
    public Account closeAccount(String accountNumber){
      Account account = findByAccountNumber(accountNumber);
      if (account.getAccountStatus() == AccountStatus.CLOSED){
          throw new RuntimeException("Account Already Closed");
      }
      account.setAccountStatus(AccountStatus.CLOSED);
      return account;
    }

    @Transactional
    public Account transfer(TransferRequest request){

        if (request.getFromAccountNumber().equals(request.getToAccountNumber())){
            throw new RuntimeException("Cannot Transfer To The Same Account");
        }
        if (request.getAmount() <= 0){
            throw new InValidAmount("Invalid Amount");
        }
        Account fromAccount = findByAccountNumber(request.getFromAccountNumber());
        Account toAccount = findByAccountNumber(request.getToAccountNumber());
        if (fromAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Sender Account Not Active");
        }
        if (toAccount.getAccountStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Reciever Account Not Active");
        }
        if (request.getAmount() > fromAccount.getBalance()){
            throw new InSufficientAmount("Insufficient Balance");
        }
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount());


        return fromAccount;
    }

}
