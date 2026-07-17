package com.ahmed.bank_api.service;


import com.ahmed.bank_api.dto.UpdateCustomerRequest;
import com.ahmed.bank_api.model.Customer;
import com.ahmed.bank_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

   @InjectMocks
    private CustomerService customerService;

   //add
   @Test
    void addCustomer_shouldSaveCustomer(){
       Customer customer = new Customer();
       customer.setFullName("Ahmed");
       customer.setId(1L);

       when(customerRepository.save(any(Customer.class)))
               .thenReturn(customer);

       Customer result = customerService.addCustomer(customer);

       assertEquals(1L , result.getId());
       assertEquals("Ahmed" , result.getFullName());


       verify(customerRepository)
               .save(customer);
   }

   //find
   @Test
    void find_shouldReturnCustomer_whenCustomerExists(){
       Customer customer = new Customer();
       customer.setId(1L);
       customer.setFullName("Ahmed");

       when(customerRepository.findById(1L))
               .thenReturn(Optional.of(customer));

       Customer result = customerService.find(1l);

       assertEquals(1L , result.getId());
       assertEquals("Ahmed" , result.getFullName());

       verify(customerRepository)
               .findById(1L);
   }

   @Test
    void find_shouldThrowException_whenCustomerNotFound(){
       when(customerRepository.findById(1L))
               .thenReturn(Optional.empty());

       RuntimeException ex = assertThrows(
               RuntimeException.class,
               ()-> customerService.find(1L)
       );

       assertEquals("Customer Not Found" , ex.getMessage());

       verify(customerRepository)
               .findById(1L);
   }

   //update
    @Test
    void update_shouldUpdateCustomer_whenCustomerExists(){
       Customer customer = new Customer();
       customer.setFullName("Ahmed");
       customer.setId(1L);

       when(customerRepository.findById(1L))
               .thenReturn(Optional.of(customer));
       when(customerRepository.save(any(Customer.class)))
               .thenReturn(customer);

        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setFullName("Mohammed");
       Customer result = customerService.update(1L , request);

       assertEquals(1L , result.getId());
       assertEquals("Mohammed", result.getFullName());

       verify(customerRepository)
               .findById(1L);
       verify(customerRepository)
               .save(customer);
    }

    @Test
    void update_shouldThrowException_whenCustomerNotFound(){
       when(customerRepository.findById(1L))
               .thenReturn(Optional.empty());
       UpdateCustomerRequest request = new UpdateCustomerRequest();
       request.setFullName("Ahmed");

       RuntimeException ex = assertThrows(
               RuntimeException.class,
               ()-> customerService.update(1L , request)
       );

       assertEquals("Customer Not Found" , ex.getMessage());

       verify(customerRepository)
               .findById(1L);
       verify(customerRepository , never())
               .save(any(Customer.class));
    }

    //delete
    @Test
    void delete_shouldDeleteCustomer_whenCustomerExists(){
       Customer customer = new Customer();
       customer.setId(1L);
       customer.setFullName("Ahmed");

       when(customerRepository.findById(1L))
               .thenReturn(Optional.of(customer));

         Customer result = customerService.deleteCustomer(1L);

         assertEquals(1L , result.getId());
         assertEquals("Ahmed" , result.getFullName());

         verify(customerRepository)
                 .findById(1L);

    }

    @Test
    void delete_shouldThrowException_whenCustomerNotFound(){
       when(customerRepository.findById(1L))
               .thenReturn(Optional.empty());

       RuntimeException ex = assertThrows(
               RuntimeException.class,
               ()->customerService.deleteCustomer(1L)
       );

       assertEquals("Customer Not Found" , ex.getMessage());

       verify(customerRepository)
               .findById(1L);

       verify(customerRepository , never())
               .save(any(Customer.class));
    }



    //getcustomers

    @Test
    void getCustomers_shouldReturnCustomers(){
       Customer c1 = new Customer();
       c1.setId(1L);
       c1.setFullName("Ahmed");

       Customer c2 = new Customer();
       c2.setId(2L);
       c2.setFullName("Mohammed");

        List<Customer> customers = List.of(c1 , c2);

        when(customerRepository.findAll())
                .thenReturn(customers);

        List<Customer> result = customerService.getCustomers();

        assertEquals(2 , result.size());
        assertEquals(customers , result);

        verify(customerRepository)
                .findAll();

    }

}
