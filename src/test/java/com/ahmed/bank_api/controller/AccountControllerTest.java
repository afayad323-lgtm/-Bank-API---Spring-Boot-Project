package com.ahmed.bank_api.controller;


import com.ahmed.bank_api.dto.AmountRequest;
import com.ahmed.bank_api.dto.CreateAccountRequest;
import com.ahmed.bank_api.dto.TransferRequest;
import com.ahmed.bank_api.exception.*;
import com.ahmed.bank_api.model.Account;
import com.ahmed.bank_api.model.AccountStatus;
import com.ahmed.bank_api.model.AccountType;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAccounts_shouldReturnAccounts() throws Exception {
        Account a1 = new Account();
        a1.setId(1L);
        a1.setAccountNumber("ACC000001");
        a1.setBalance(2000);

        Account a2 = new Account();
        a2.setId(2L);
        a2.setAccountNumber("ACC000002");
        a2.setBalance(1000);

        List<Account> accounts = List.of(a1 , a2);

        when(accountService.getAccounts())
                .thenReturn(accounts);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].balance").value(2000))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$.length()").value(2));

        verify(accountService)
                .getAccounts();

    }

    @Test
    void getOneAccount_shouldReturnOneAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC000001");
        account.setBalance(2000);

        when(accountService.findByAccountNumber("ACC000001"))
                .thenReturn(account);

        mockMvc.perform(get("/accounts/{accountNumber}","ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.balance").value(2000));

        verify(accountService)
                .findByAccountNumber("ACC000001");
    }

    @Test
    void getOneAccount_shouldReturnException() throws Exception {
        when(accountService.findByAccountNumber("ACC000001"))
                .thenThrow(new AccountNotFound("Account Not Found"));

        mockMvc.perform(get("/accounts/{id}","ACC000001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Not Found"));

        verify(accountService)
                .findByAccountNumber("ACC000001");
    }

    @Test
    void createAccount_shouldCreateAccount() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFullName("Ahmed");

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC000001");
        account.setCustomer(customer);
        account.setBalance(2000);
        account.setAccountStatus(AccountStatus.ACTIVE);


        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);


        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(account);

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(2000));

        verify(accountService)
                .createAccount(any(CreateAccountRequest.class));


    }

    @Test
    void createAccount_shouldReturnException404() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);

        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenThrow(new CustomerNotFound("Customer Not Found"));

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer Not Found"));

        verify(accountService)
                .createAccount(any(CreateAccountRequest.class));


    }

    @Test
    void createAccount_shouldReturnException409() throws Exception {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);

        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenThrow(new DuplicateAccount("Customer Already Has This Account Type"));

        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Customer Already Has This Account Type"));

        verify(accountService)
                .createAccount(any(CreateAccountRequest.class));

    }

    @Test
    void deposit_shouldIncreaseBalance_whenAmountIsValid() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFullName("Ahmed");

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC000001");
        account.setCustomer(customer);
        account.setBalance(2500);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountType(AccountType.SAVINGS);

        AmountRequest request = new AmountRequest();
        request.setAmount(500);



        when(accountService.deposit("ACC000001" , 500))
                .thenReturn(account);

        mockMvc.perform(post("/accounts/{accountNumber}/deposit", "ACC000001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.balance").value(2500));

        verify(accountService)
                .deposit("ACC000001" , 500);
    }

    @Test
    void deposit_shouldReturnException400_whenAmountIsInvalid() throws Exception {
        when(accountService.deposit("ACC000001" , -100))
                .thenThrow(new InValidAmount("Invalid Amount"));

        AmountRequest request = new AmountRequest();
        request.setAmount(-100);

        mockMvc.perform(post("/accounts/{accountNumber}/deposit" , "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Amount"));

        verify(accountService)
                .deposit("ACC000001" , -100);

    }

    @Test
    void deposit_shouldReturnException404_whenAccountNotFound() throws Exception {
        when(accountService.deposit("ACC000001" , 500))
                .thenThrow(new AccountNotFound("Account Not Found"));
        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        mockMvc.perform(post("/accounts/{accountNumber}/deposit", "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Not Found"));

        verify(accountService)
                .deposit("ACC000001" , 500);


    }

    @Test
    void deposit_shouldReturn409_whenAccountIsInActive() throws Exception {
        when(accountService.deposit("ACC000001" , 500))
                .thenThrow(new AccountNotActive("Account Not Active"));
        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        mockMvc.perform(post("/accounts/{accountNumber}/deposit" , "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account Not Active"));

        verify(accountService)
                .deposit("ACC000001" , 500);

    }

    @Test
    void withdraw_shouldDecreaseBalance_whenAmountIsValid() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFullName("Ahmed");

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC000001");
        account.setCustomer(customer);
        account.setBalance(2500);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountType(AccountType.SAVINGS);

        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        when(accountService.withdraw("ACC000001" , 500))
                .thenReturn(account);

        mockMvc.perform(post("/accounts/{accountNumber}/withdraw" , "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.balance").value(2500));

        verify(accountService)
                .withdraw("ACC000001" , 500);

    }

    @Test
    void withdraw_shouldReturn400_whenAmountIsInvalid() throws Exception {
        when(accountService.withdraw("ACC000001" , -100))
                .thenThrow(new InValidAmount("Invalid Amount"));
        AmountRequest request = new AmountRequest();
        request.setAmount(-100);

        mockMvc.perform(post("/accounts/{accountNumber}/withdraw", "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Amount"));

        verify(accountService)
                .withdraw("ACC000001" , -100);
    }

    @Test
    void withdraw_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.withdraw("ACC000001" , 500))
                .thenThrow(new AccountNotFound("Account Not Found"));
        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        mockMvc.perform(post("/accounts/{accountNumber}/withdraw" ,"ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Not Found"));
        verify(accountService)
                .withdraw("ACC000001" , 500);
    }

    @Test
    void withdraw_shouldReturn409_whenAccountNotActive() throws Exception {
        when(accountService.withdraw("ACC000001" , 500))
                .thenThrow(new AccountNotActive("Account Not Active"));
        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        mockMvc.perform(post("/accounts/{accountNumber}/withdraw" , "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account Not Active"));
        verify(accountService)
                .withdraw("ACC000001" , 500);
    }

    @Test
    void withdraw_shouldReturn409_whenBalanceInsufficient() throws Exception {
        when(accountService.withdraw("ACC000001" , 500))
                .thenThrow(new InSufficientAmount("Amount Insufficient"));

        AmountRequest request = new AmountRequest();
        request.setAmount(500);

        mockMvc.perform(post("/accounts/{accountNumber}/withdraw" , "ACC000001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Amount Insufficient"));
        verify(accountService)
                .withdraw("ACC000001" , 500);
    }

    @Test
    void transfer_shouldTransferBetweenAccounts_whenAmountIsValid() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setBalance(1000);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenReturn(account);

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC000001"))
                .andExpect(jsonPath("$.balance").value(1000));
        verify(accountService)
                .transfer(any(TransferRequest.class));

    }

    @Test
    void transfer_shouldReturn400_whenTransferToSameAccount() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000001");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new RuntimeException("Cannot Transfer To The Same Account"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());


        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn400_whenAmountIsInvalid() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(-100);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new InValidAmount("Invalid Amount"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Amount"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn409_whenBalanceIsInsufficient() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new InSufficientAmount("Insufficient Balance"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Insufficient Balance"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn404_whenSenderNotFound() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000001"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Not Found: ACC000001"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn404_whenRecieverNotFound() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000002"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account Not Found: ACC000002"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn409_whenSenderIsNotActive() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new AccountNotActive("Account Not Active"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account Not Active"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void transfer_shouldReturn409_whenReceiverIsNotActive() throws Exception {
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC000001");
        request.setToAccountNumber("ACC000002");
        request.setAmount(500);

        when(accountService.transfer(any(TransferRequest.class)))
                .thenThrow(new AccountNotActive("Account Not Active"));

        mockMvc.perform(post("/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account Not Active"));

        verify(accountService)
                .transfer(any(TransferRequest.class));
    }

    @Test
    void blockAccount_shouldBlockAccount_whenAccountIsActive() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setAccountStatus(AccountStatus.BLOCKED);

        when(accountService.blockAccount("ACC000001"))
                .thenReturn(account);

        mockMvc.perform(patch("/accounts/{accountNumber}/block","ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("BLOCKED"));

        verify(accountService)
                .blockAccount("ACC000001");

    }

    @Test
    void blockAccount_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.blockAccount("ACC000001"))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000001"));

        mockMvc.perform(patch("/accounts/{accountNumber}/block" , "ACC000001"))
                .andExpect(status().isNotFound());

        verify(accountService)
                .blockAccount("ACC000001");
    }

    @Test
    void blockAccount_shouldReturn409_whenAccountIsBlocked() throws Exception {
        when(accountService.blockAccount("ACC000001"))
                .thenThrow(new RuntimeException("Account Is Already Blocked"));

        mockMvc.perform(patch("/accounts/{accountNumber}/block" , "ACC000001"))
                .andExpect(status().isInternalServerError());
        verify(accountService)
                .blockAccount("ACC000001");
    }

    @Test
    void blockAccount_shouldReturn409_whenAccountClosed() throws Exception {
        when(accountService.blockAccount("ACC000001"))
                .thenThrow(new RuntimeException("Closed Account Cannot Be Blocked"));

        mockMvc.perform(patch("/accounts/{accountNumber}/block" , "ACC000001"))
                .andExpect(status().isInternalServerError());

        verify(accountService)
                .blockAccount("ACC000001");
    }

    @Test
    void activateAccount_shouldActivateAccount_whenAccountIsBlocked() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setAccountStatus(AccountStatus.ACTIVE);

        when(accountService.activateAccount("ACC000001"))
                .thenReturn(account);

        mockMvc.perform(patch("/accounts/{accountNumber}/activate" , "ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("ACTIVE"));

        verify(accountService)
                .activateAccount("ACC000001");
    }

    @Test
    void activateAccount_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.activateAccount("ACC000001"))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000001"));

        mockMvc.perform(patch("/accounts/{accountNumber}/activate", "ACC000001"))
                .andExpect(status().isNotFound());

        verify(accountService)
                .activateAccount("ACC000001");
    }

    @Test
    void activateAccount_shouldReturn409_whenAccountClosed() throws Exception {
        when(accountService.activateAccount("ACC000001"))
                .thenThrow(new RuntimeException("Closed Account Cannot Be Activated"));

        mockMvc.perform(patch("/accounts/{accountNumber}/activate","ACC000001"))
                .andExpect(status().isInternalServerError());

        verify(accountService)
                .activateAccount("ACC000001");
    }

    @Test
    void activateAccount_shouldReturn409_whenAccountActive() throws Exception {
        when(accountService.activateAccount("ACC000001"))
                .thenThrow(new RuntimeException("Account is Already Active"));

        mockMvc.perform(patch("/accounts/{accountNumber}/activate" , "ACC000001"))
                .andExpect(status().isInternalServerError());

        verify(accountService)
                .activateAccount("ACC000001");
    }

    @Test
    void closeAccount_shouldCloseAccount_whenAccountIsActive() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC000001");
        account.setAccountStatus(AccountStatus.CLOSED);

        when(accountService.closeAccount("ACC000001"))
                .thenReturn(account);

        mockMvc.perform(patch("/accounts/{accountNumber}/close","ACC000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountStatus").value("CLOSED"));

        verify(accountService)
                .closeAccount("ACC000001");
    }

    @Test
    void closeAccount_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.closeAccount("ACC000001"))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000001"));
        mockMvc.perform(patch("/accounts/{accountNumber}/close","ACC000001"))
                .andExpect(status().isNotFound());

        verify(accountService)
                .closeAccount("ACC000001");
    }

    @Test
    void closeAccount_shouldReturn409_whenAccountIsClosed() throws Exception {
        when(accountService.closeAccount("ACC000001"))
                .thenThrow(new RuntimeException("Account is already closed"));

        mockMvc.perform(patch("/accounts/{accountNumber}/close" , "ACC000001"))
                .andExpect(status().isInternalServerError());

        verify(accountService)
                .closeAccount("ACC000001");
    }



    @Test
    void deleteAccount_shouldDeleteAccount_whenAccountExists() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC000001");

        when(accountService.delete("ACC000001"))
                .thenReturn(account);
        mockMvc.perform(delete("/accounts/{accountNumber}" , "ACC000001"))
                .andExpect(status().isOk());
        verify(accountService)
                .delete("ACC000001");
    }

    @Test
    void deleteAccount_shouldReturn404_whenAccountNotFound() throws Exception {
        when(accountService.delete("ACC000001"))
                .thenThrow(new AccountNotFound("Account Not Found: ACC000001"));

        mockMvc.perform(delete("/accounts/{accountNumber}" , "ACC000001"))
                .andExpect(status().isNotFound());

        verify(accountService)
                .delete("ACC000001");
    }




}
