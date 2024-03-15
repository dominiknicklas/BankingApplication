package com.dnicklas.bankingapplication.repository;

import com.dnicklas.bankingapplication.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT u FROM Customer u WHERE u.accountId = ?1")
    Customer findByAccountId(Long id);
}
