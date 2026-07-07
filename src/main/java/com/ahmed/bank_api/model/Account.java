package com.ahmed.bank_api.model;
import jakarta.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double balance;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Account(){}

//    public Account(Customer customer , double amount){
//        this.customer = customer;
//        this.balance = 0;
//    }



    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getBalance() {
        return balance;
    }

    public long getId() {
        return id;
    }
}
