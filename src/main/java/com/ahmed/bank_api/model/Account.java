package com.ahmed.bank_api.model;
import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
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

    public long getId() {
        return id;
    }
}
