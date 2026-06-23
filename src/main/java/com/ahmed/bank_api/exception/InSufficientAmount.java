package com.ahmed.bank_api.exception;

public class InSufficientAmount extends RuntimeException{
    public InSufficientAmount(String message){
        super(message);
    }
}
