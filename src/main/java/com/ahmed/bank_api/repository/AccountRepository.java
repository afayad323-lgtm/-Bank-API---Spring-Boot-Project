package com.ahmed.bank_api.repository;
import com.ahmed.bank_api.model.Account;
import com.ahmed.bank_api.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long>{
    boolean existsByCustomerIdAndAccountType(Long customerId,
                                             AccountType accountType);

}
