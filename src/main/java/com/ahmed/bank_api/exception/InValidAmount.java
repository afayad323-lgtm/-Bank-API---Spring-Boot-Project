package com.ahmed.bank_api.exception;

public class InValidAmount extends RuntimeException{
    public InValidAmount(String message){
        super(message);
    }
}
