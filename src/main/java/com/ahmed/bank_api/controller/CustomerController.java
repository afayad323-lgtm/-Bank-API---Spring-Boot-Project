package com.ahmed.bank_api.controller;

import com.ahmed.bank_api.dto.UpdateCustomerRequest;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers(){
        return customerService.getCustomers();
    }

    @GetMapping("/customers/{id}")
    public Customer getOneCustomer(@PathVariable Long id){
        return customerService.find(id);
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer){
        return customerService.addCustomer(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody UpdateCustomerRequest request){
        return customerService.update(id , request);

    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
         customerService.deleteCustomer(id);

    }
}
