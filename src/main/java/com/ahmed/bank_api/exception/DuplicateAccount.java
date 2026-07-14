package com.ahmed.bank_api.exception;

public class DuplicateAccount extends RuntimeException{

    public DuplicateAccount(String message){
        super(message);
    }
}
