package com.ahmed.bank_api.exception;

public class AccountNotActive extends RuntimeException{
    public AccountNotActive(String message){
        super(message);
    }
}
