package com.ahmed.bank_api.repository;
import com.ahmed.bank_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer , Long>{
}
