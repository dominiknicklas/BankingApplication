package com.dnicklas.bankingapplication.controller;

import com.dnicklas.bankingapplication.model.ActionAmount;
import com.dnicklas.bankingapplication.model.Customer;
import com.dnicklas.bankingapplication.repository.CustomerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.text.NumberFormat;

@Controller
@RequestMapping("/account/actions")
public class ActionsController {
    private CustomerRepository repo;

    public ActionsController(CustomerRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/processDeposit")
    public String depositMoney(@ModelAttribute("actionAmount") ActionAmount amount, Model model) {
        BigDecimal depositAmount = getActionAmount(amount);

        Customer customer = repo.findByAccountId(amount.getAccountId());
        BigDecimal currentBalance = customer.getBalance();
        BigDecimal newBalance = currentBalance.add(depositAmount);
        customer.setBalance(newBalance);
        Customer updatedCustomer = repo.save(customer);

        setUpModel(model, updatedCustomer);
        model.addAttribute("message", "Deposit successful!");
        return "account";
    }

    @PostMapping("/processWithdraw")
    public String withdrawMoney(@ModelAttribute("actionAmount") ActionAmount amount, Model model) {
        BigDecimal withdrawAmount = getActionAmount(amount);

        Customer customer = repo.findByAccountId(amount.getAccountId());
        BigDecimal currentBalance = customer.getBalance();
        if(currentBalance.compareTo(withdrawAmount) < 0) {
            model.addAttribute("amount", withdrawAmount);
            setUpModel(model, customer);
            return "account-withdraw-denied";
        }

        BigDecimal newBalance = currentBalance.subtract(withdrawAmount);
        customer.setBalance(newBalance);
        Customer updatedCustomer = repo.save(customer);
        setUpModel(model, updatedCustomer);
        model.addAttribute("message", "Withdraw successful!");
        return "account";
    }

    @PostMapping("/processTransaction")
    public String transferMoney(@ModelAttribute("actionAmount") ActionAmount actionAmount, Model model) {
        BigDecimal transferAmount = getActionAmount(actionAmount);
        Customer customer = repo.findByAccountId(actionAmount.getAccountId());
        BigDecimal currentBalanceCustomer = customer.getBalance();

        Customer otherCustomer = repo.findByAccountId(actionAmount.getOtherAccountId());
        if(currentBalanceCustomer.compareTo(transferAmount) < 0 || otherCustomer == null || customer.getAccountId().equals(otherCustomer.getAccountId())) {
            setUpModel(model, customer);
            return "account-transfer-denied";
        }

        BigDecimal currentBalanceOtherCustomer = otherCustomer.getBalance();
        customer.setBalance(currentBalanceCustomer.subtract(transferAmount));
        otherCustomer.setBalance(currentBalanceOtherCustomer.add(transferAmount));
        Customer updatedCustomer = repo.save(customer);
        repo.save(otherCustomer);

        String formattedMoney = NumberFormat.getCurrencyInstance().format(transferAmount);
        String s = transferAmount.equals(BigDecimal.ONE) ? "has" : "have";
        String message = String.format("Transfer successful! - %s %s been transferred to the account with account number %d", formattedMoney, s, actionAmount.getOtherAccountId());
        model.addAttribute("message", message);
        setUpModel(model, updatedCustomer);
        return "account";
    }

    @PostMapping("/backToAccount")
    public String returnToAccount(@ModelAttribute("actionAmount") ActionAmount account, Model model) {
        Long accountId = account.getAccountId();
        Customer customer = repo.findByAccountId(accountId);
        setUpModel(model, customer);
        model.addAttribute("message", "Notting happened!");
        return "account";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@ModelAttribute("actionAmount") ActionAmount actionAmount, Model model) {
        Long accountId = actionAmount.getAccountId();
        Customer customer = repo.findByAccountId(accountId);
        setUpModel(model, customer);
        if(customer.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            model.addAttribute("deleteMessage", "You cannot delete the account because there is money in it. Please withdraw or transfer the funds and try again!");
            return "account";
        }
        return "delete-confirmation";
    }

    @PostMapping("delete")
    public String delete(@ModelAttribute("actionAmount") ActionAmount actionAmount) {
        Customer customer = repo.findByAccountId(actionAmount.getAccountId());
        repo.delete(customer);
        return "home";
    }

    private BigDecimal getActionAmount(ActionAmount actionAmount) {
        String sum = actionAmount.getAmount();
        sum = sum.replace(',', '.');
        return new BigDecimal(sum);
    }

    private void setUpModel(Model model, Customer customer) {
        model.addAttribute("customer", customer);
        model.addAttribute("actionAmount", new ActionAmount());
    }
}