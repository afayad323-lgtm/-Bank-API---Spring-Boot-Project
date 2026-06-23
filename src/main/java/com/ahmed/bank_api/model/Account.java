package com.ahmed.bank_api.model;

public class Account {
    private String ownerName;
    private double balance;

    public Account(){}

    public Account(String name , double amount){
        this.ownerName = name;
        this.balance = amount;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }


}
