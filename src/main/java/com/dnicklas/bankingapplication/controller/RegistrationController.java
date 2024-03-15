package com.dnicklas.bankingapplication.controller;

import com.dnicklas.bankingapplication.model.ActionAmount;
import com.dnicklas.bankingapplication.model.Customer;
import com.dnicklas.bankingapplication.repository.CustomerRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private CustomerRepository repo;

    public RegistrationController(CustomerRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/processRegistration")
    public String processRegister(@ModelAttribute("customer") Customer customer, Model model) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);

        Customer savedCustomer = repo.save(customer);
        model.addAttribute("account_id", savedCustomer.getAccountId());

        return "register-success";
    }

    @GetMapping("/account")
    public String showAccount(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long account_id = Long.parseLong(userDetails.getUsername());
        Customer customer = repo.findByAccountId(account_id);
        model.addAttribute("customer", customer);
        model.addAttribute("actionAmount", new ActionAmount());
        model.addAttribute("message", "");
        return "account";
    }

}
