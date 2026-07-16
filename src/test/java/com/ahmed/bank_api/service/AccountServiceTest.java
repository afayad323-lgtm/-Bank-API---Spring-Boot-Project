package com.ahmed.bank_api.service;

import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.dto.TransferRequest;
import com.ahmed.bank_api.exception.*;
import com.ahmed.bank_api.model.Account;
import com.ahmed.bank_api.model.AccountStatus;
import com.ahmed.bank_api.model.AccountType;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.repository.AccountRepository;
import com.ahmed.bank_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
        private AccountRepository accountRepository;

    @Mock
        private CustomerRepository customerRepository;

    @InjectMocks
        private AccountService accountService;

    //deposit
    @Test
        void deposit_shouldIncreaseBalance_whenAmountIsValid(){
        //Arrange
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setBalance(1000);
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

        //ACT
        Account result = accountService.deposit("ACC000001", 500);

        //Assert
        assertEquals(1500,result.getBalance());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");


    }

    @Test
        void deposit_shouldThrowException_whenAmountIsInvalid(){
        InValidAmount ex = assertThrows(
                InValidAmount.class,
                () -> accountService.deposit("ACC000001" , -100)

        );
        assertEquals("Invalid Amount" , ex.getMessage());
        verify(accountRepository , never())
                .findByAccountNumber("ACC000001");
    }

    @Test
        void deposit_shouldThrowException_whenAccountIsNotActive(){
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setBalance(1000);
        account.setAccountStatus(AccountStatus.BLOCKED);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

        AccountNotActive ex = assertThrows(
                AccountNotActive.class,
                ()-> accountService.deposit("ACC000001" , 500)
        );

        assertEquals("Account Is Not Active" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");

        verify(accountRepository, never())
                .save(any());

    }

    @Test
        void deposit_shouldThrowException_whenAccountIsNotFound(){
        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.empty());

        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.deposit("ACC000001",500)
        );

        assertEquals("Account Not Found: ACC000001" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
    }

    //withdraw
    @Test
        void withdraw_shouldDecreaseBalance_whenAmountIsValid(){
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setBalance(1000);
        account.setAccountStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

        Account result = accountService.withdraw("ACC000001" , 500);

        assertEquals(500 , result.getBalance());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
}

    @Test
        void withdraw_shouldThrowException_whenAmountIsNotValid(){
        InValidAmount ex = assertThrows(
                InValidAmount.class,
                ()-> accountService.withdraw("ACC000001" , -100)
        );
        assertEquals("Invalid Amount" , ex.getMessage());

        verify(accountRepository , never())
                .findByAccountNumber("ACC000001");
}

    @Test
        void withdraw_shouldThrowException_whenAccountIsNotFound(){

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.empty());

        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.withdraw("ACC000001" , 500)
        );


                assertEquals("Account Not Found: ACC000001" , ex.getMessage());
                verify(accountRepository)
                .findByAccountNumber("ACC000001");
            }

    @Test
        void withdraw_shouldThrowException_whenAccountIsNotActive(){
            Account account = new Account();
            account.setBalance(1000);
            account.setAccountNumber("ACC000001");
            account.setAccountStatus(AccountStatus.BLOCKED);

             when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

            AccountNotActive ex = assertThrows(
                AccountNotActive.class,
                () -> accountService.withdraw("ACC000001" , 500)
            );
             assertEquals("Account Is Not Active" , ex.getMessage());

             verify(accountRepository)
                .findByAccountNumber("ACC000001");
              verify(accountRepository , never())
                .save(any());
            }

    @Test
        void withdraw_shouldThrowException_whenBalanceIsNotSufficient(){
            Account account = new Account();
            account.setAccountStatus(AccountStatus.ACTIVE);
            account.setBalance(200);
            account.setAccountNumber("ACC000001");

            when(accountRepository.findByAccountNumber("ACC000001"))
                    .thenReturn(Optional.of(account));

        InSufficientAmount ex = assertThrows(
                InSufficientAmount.class,
                ()-> accountService.withdraw("ACC000001" , 500)
        );

        assertEquals("Insufficient Balance" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
    }

    //transfer
    @Test
        void transfer_shouldTransferBalance_whenAmountIsValid() {
        Account fromAccount = new Account();
        fromAccount.setAccountNumber("ACC000001");
        fromAccount.setBalance(1000);
        fromAccount.setAccountStatus(AccountStatus.ACTIVE);

        Account toAccount = new Account();
        toAccount.setAccountNumber("ACC000002");
        toAccount.setBalance(300);
        toAccount.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC000002"))
                .thenReturn(Optional.of(toAccount));

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(300);

        Account result = accountService.transfer(request);

        assertEquals(700, fromAccount.getBalance());
        assertEquals(600, toAccount.getBalance());
        assertEquals(700, result.getBalance());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository)
                .findByAccountNumber("ACC000002");

        verify(accountRepository, never())
                .save(any());
    }

    @Test
        void transfer_shouldThrowException_whenTransferToSameAccount(){
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000001");
        request.setAmount(300);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Cannot Transfer To The Same Account" , ex.getMessage());

        verifyNoInteractions(accountRepository);
    }

    @Test
        void transfer_shouldThrowException_whenAmountIsNotValid(){
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(-100);

        InValidAmount ex = assertThrows(
                InValidAmount.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Invalid Amount" , ex.getMessage());

        verifyNoInteractions(accountRepository);


    }

    @Test
        void transfer_shouldThrowException_whenSenderAccountIsNotActive(){
        Account fromAccount = new Account();
        fromAccount.setAccountStatus(AccountStatus.BLOCKED);
        fromAccount.setBalance(200);
        fromAccount.setAccountNumber("ACC000001");

        Account toAccount = new Account();
        toAccount.setAccountNumber("ACC000002");
        toAccount.setBalance(300);
        toAccount.setAccountStatus(AccountStatus.ACTIVE);

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(100);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(fromAccount));


        AccountNotActive ex = assertThrows(
                AccountNotActive.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Account Is Not Active" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository , never())
                .findByAccountNumber("ACC000002");

    }

    @Test
        void transfer_shouldThrowException_whenReceiverIsNotActive(){
        Account fromAccount = new Account();
        fromAccount.setAccountStatus(AccountStatus.ACTIVE);
        fromAccount.setBalance(200);
        fromAccount.setAccountNumber("ACC000001");

        Account toAccount = new Account();
        toAccount.setAccountNumber("ACC000002");
        toAccount.setBalance(300);
        toAccount.setAccountStatus(AccountStatus.CLOSED);

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(100);


        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC000002"))
                .thenReturn(Optional.of(toAccount));

        AccountNotActive ex = assertThrows(
                AccountNotActive.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Account Is Not Active" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository)
                .findByAccountNumber("ACC000002");



    }

    @Test
        void transfer_shouldThrowException_whenBalanceIsNotSufficient(){
        Account fromAccount = new Account();
        fromAccount.setAccountNumber("ACC000001");
        fromAccount.setBalance(1000);
        fromAccount.setAccountStatus(AccountStatus.ACTIVE);

        Account toAccount = new Account();
        toAccount.setAccountNumber("ACC000002");
        toAccount.setBalance(300);
        toAccount.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC000002"))
                .thenReturn(Optional.of(toAccount));


        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(3000);

        InSufficientAmount ex = assertThrows(
                InSufficientAmount.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Insufficient Balance" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository)
                .findByAccountNumber("ACC000002");






    }

    @Test
        void transfer_shouldThrowException_whenSenderIsNotFound(){
            when(accountRepository.findByAccountNumber("ACC000001"))
                    .thenReturn(Optional.empty());

            TransferRequest request = new TransferRequest();
            request.setFromAccountNumber("ACC000001");
            request.setToAccountNumber("ACC000002");
            request.setAmount(200);

            AccountNotFound ex = assertThrows(
                    AccountNotFound.class,
                    ()-> accountService.transfer(request)
            );

            assertEquals("Account Not Found: ACC000001" , ex.getMessage());

            verify(accountRepository)
                    .findByAccountNumber("ACC000001");
            verify(accountRepository , never())
                    .findByAccountNumber("ACC000002");

    }

    @Test
        void transfer_shouldThrowException_whenReceiverIsNotFound(){
        Account fromAccount = new Account();
        fromAccount.setAccountStatus(AccountStatus.ACTIVE);
        fromAccount.setBalance(200);
        fromAccount.setAccountNumber("ACC000001");

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber("ACC000002"))
                .thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(100);

        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.transfer(request)
        );

        assertEquals("Account Not Found: ACC000002" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository)
                .findByAccountNumber("ACC000002");

    }


    //create account
    @Test
        void createAccount_shouldCreateAccount_whenRequestIsValid(){
        Customer customer = new Customer();
        customer.setFullName("Ahmed");
        customer.setId(1L);

        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.existsByCustomerIdAndAccountType(1L , AccountType.SAVINGS))
                .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account account = invocation.getArgument(0);
                    account.setId(1L);
                    return account;
                });

        Account result = accountService.createAccount(request);

        assertEquals(0 , result.getBalance());
        assertEquals(customer , result.getCustomer());
        assertEquals(AccountType.SAVINGS , result.getAccountType());
        assertEquals(AccountStatus.ACTIVE , result.getAccountStatus());
        assertEquals("ACC000001" , result.getAccountNumber());

        verify(customerRepository)
                .findById(1L);
        verify(accountRepository)
                .existsByCustomerIdAndAccountType(1L , AccountType.SAVINGS);
        verify(accountRepository)
                .save(any(Account.class));



    }

    @Test
        void createAccount_shouldThrowException_whenCustomerNotFound(){
        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);

        CustomerNotFound ex = assertThrows(
                CustomerNotFound.class,
                ()-> accountService.createAccount(request)
        );

        assertEquals("Customer Not Found" , ex.getMessage());

        verify(customerRepository)
                .findById(1L);
        verifyNoInteractions(accountRepository);
    }

    @Test
        void createAccount_shouldThrowException_whenDuplicateAccountType(){
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFullName("Ahmed");

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);
        request.setCustomerId(1L);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));
        when(accountRepository.existsByCustomerIdAndAccountType(1L,AccountType.SAVINGS))
                .thenReturn(true);

        DuplicateAccount ex = assertThrows(
                DuplicateAccount.class,
                ()->accountService.createAccount(request)
        );

        assertEquals("Customer already has this account type" , ex.getMessage());


        verify(customerRepository)
                .findById(1L);
        verify(accountRepository)
                .existsByCustomerIdAndAccountType(1L , AccountType.SAVINGS);
        verify(accountRepository, never())
                .save(any(Account.class));
    }


    //getAllAccounts
    @Test
        void getAllAccounts_shouldReturnAllAccounts(){
        Account account1 = new Account();
        account1.setId(1L);

        Account account2 = new Account();
        account2.setId(2L);

        List<Account> accounts = List.of(account1 , account2);

        when(accountRepository.findAll())
                .thenReturn(accounts);

        List<Account> result = accountService.getAccounts();

        assertEquals(2 , result.size());
        assertEquals(accounts,result);

        verify(accountRepository)
                .findAll();
    }

    //findByAccountNumber
    @Test
        void findByAccountNumber_shouldFindAccountByAccountNumber(){
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setBalance(1000);
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

        Account result = accountService.findByAccountNumber("ACC000001");

        assertEquals("ACC000001" , result.getAccountNumber());
        assertEquals(1000 , result.getBalance());
        assertEquals(AccountStatus.ACTIVE , result.getAccountStatus());
        assertEquals(account , result);
        verify(accountRepository)
                .findByAccountNumber("ACC000001");
    }

    @Test
        void findByAccountNumber_shouldThrowException_whenAccountNotFound(){
        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.empty());

        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.findByAccountNumber("ACC000001")
        );

        assertEquals("Account Not Found: ACC000001" ,ex.getMessage() );

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
    }

    //delete
    @Test
        void deleteAccount_shouldDeleteAccount_whenAccountExists(){
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(1000);

        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.of(account));

        Account result = accountService.delete("ACC000001");

        assertEquals(account , result);

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository)
                .delete(account);
    }
    @Test
        void deleteAccount_shouldThrowException_whenAccountNotFound(){
        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.empty());

        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.delete("ACC000001")
        );
        assertEquals("Account Not Found: ACC000001" , ex.getMessage());

        verify(accountRepository)
                .findByAccountNumber("ACC000001");
        verify(accountRepository , never())
                .delete(any(Account.class));
    }

    


}
