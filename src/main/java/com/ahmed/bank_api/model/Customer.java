package com.ahmed.bank_api.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @OneToMany(mappedBy = "customer")
    private List<Account> accounts = new ArrayList<>();

    public Customer(){}

    public Customer(String name){
        this.fullName = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
