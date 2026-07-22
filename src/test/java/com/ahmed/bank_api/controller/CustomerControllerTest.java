package com.ahmed.bank_api.controller;

import com.ahmed.bank_api.dto.UpdateCustomerRequest;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.service.CustomerService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.util.List;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCustomers_shouldReturnAllCustomers() throws Exception {
    Customer c1 = new Customer();
    c1.setId(1L);
    c1.setFullName("Ahmed");

    Customer c2 = new Customer();
    c2.setId(2L);
    c2.setFullName("Mohammed");

    List<Customer> customers = List.of(c1 , c2);

    when(customerService.getCustomers())
            .thenReturn(customers);

    mockMvc.perform(get("/customers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].fullName").value("Ahmed"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].fullName").value("Mohammed"));

    verify(customerService)
            .getCustomers();



    }

    @Test
    void getOneCustomer_shouldReturnOneCustomer() throws Exception {
     Customer customer = new Customer();
     customer.setId(1L);
     customer.setFullName("Ahmed");

     when(customerService.find(1L))
             .thenReturn(customer);

     mockMvc.perform(get("/customers/{id}",1L))
             .andExpect(status().isOk())
             .andExpect(jsonPath("$.id").value(1))
             .andExpect(jsonPath("$.fullName").value("Ahmed"));

     verify(customerService)
             .find(1L);
    }

    @Test
    void getOneCustomer_shouldThrowError_whenCustomerNotFound() throws Exception {
        when(customerService.find(1L))
                .thenThrow(new RuntimeException("Customer Not Found"));

        mockMvc.perform(get("/customers/{id}", 1L))
                .andExpect(status().isInternalServerError());

        verify(customerService)
                .find(1L);

    }

    @Test
    void addCustomer_shouldAddCustomer() throws Exception {
      Customer customer = new Customer();
      customer.setId(1L);
      customer.setFullName("Ahmed");

      when(customerService.addCustomer(any(Customer.class)))
              .thenReturn(customer);

      mockMvc.perform(post("/customers")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(customer)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(1))
              .andExpect(jsonPath("$.fullName").value("Ahmed"));

      verify(customerService)
              .addCustomer(any(Customer.class));

    }

    @Test
    void updateCustomer_shouldUpdateCustomer() throws Exception {
    UpdateCustomerRequest request = new UpdateCustomerRequest();
    request.setFullName("Ahmed");

    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFullName("Mohammed");

    when(customerService.update(eq(1L) , any(UpdateCustomerRequest.class)))
            .thenReturn(customer);

    mockMvc.perform(put("/customers/{id}",1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.fullName").value("Mohammed"));

    verify(customerService)
            .update(eq(1L) , any(UpdateCustomerRequest.class));





    }

    @Test
    void deleteCustomer_shouldDeleteCustomer() throws Exception {
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setFullName("Ahmed");

    mockMvc.perform(delete("/customers/{id}", 1L))
            .andExpect(status().isOk());

    verify(customerService)
            .deleteCustomer(1L);


    }




}
