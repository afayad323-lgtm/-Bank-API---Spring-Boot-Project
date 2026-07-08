package com.ahmed.bank_api.dto;

import com.ahmed.bank_api.model.AccountStatus;
import com.ahmed.bank_api.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequest {
@NotNull
    private Long customerId;
private AccountType accountType;



    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }


}
