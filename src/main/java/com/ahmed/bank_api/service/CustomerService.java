package com.ahmed.bank_api.service;


import com.ahmed.bank_api.dto.UpdateCustomerRequest;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers(){
        return customerRepository.findAll();
    }

    public Customer addCustomer(Customer customer){
        return customerRepository.save(customer);
    }

    public Customer find(Long id){
        return customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not Found"));
    }

    public Customer update(Long id , UpdateCustomerRequest request){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not Found"));
        customer.setFullName(request.getFullName());
        return customerRepository.save(customer);

    }

    public Customer deleteCustomer(Long id){
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not Found"));
        customerRepository.delete(customer);

        return customer;

    }
}
