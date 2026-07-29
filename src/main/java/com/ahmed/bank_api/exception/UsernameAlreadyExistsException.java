package com.ahmed.bank_api.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username){
        super("Username '" + username + "' already exists");
    }
}
