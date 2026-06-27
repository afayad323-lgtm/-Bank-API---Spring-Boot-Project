package com.ahmed.bank_api.repository;
import com.ahmed.bank_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long>{


}
