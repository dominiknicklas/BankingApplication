package com.dnicklas.bankingapplication.security;

import com.dnicklas.bankingapplication.model.Customer;
import com.dnicklas.bankingapplication.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomCustomerDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository repo;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Customer customer = null;
        try {
            customer = repo.findByAccountId(Long.parseLong(id));
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("User not found");
        }
        if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomCustomerDetails(customer);
    }
}
