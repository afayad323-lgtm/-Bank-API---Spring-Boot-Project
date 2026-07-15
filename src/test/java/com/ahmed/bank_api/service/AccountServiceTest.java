package com.ahmed.bank_api.service;

import com.ahmed.bank_api.dto.TransferRequest;
import com.ahmed.bank_api.exception.AccountNotActive;
import com.ahmed.bank_api.exception.AccountNotFound;
import com.ahmed.bank_api.exception.InSufficientAmount;
import com.ahmed.bank_api.exception.InValidAmount;
import com.ahmed.bank_api.model.Account;
import com.ahmed.bank_api.model.AccountStatus;
import com.ahmed.bank_api.repository.AccountRepository;
import com.ahmed.bank_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        AccountNotFound ex = assertThrows(
                AccountNotFound.class,
                ()-> accountService.withdraw("ACC000001" , 500)
        );
        when(accountRepository.findByAccountNumber("ACC000001"))
                .thenReturn(Optional.empty());

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



}
