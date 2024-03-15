package com.dnicklas.bankingapplication.model;

public class ActionAmount {
    private Long accountId;
    private String amount;

    private Long otherAccountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Long getOtherAccountId() {
        return otherAccountId;
    }

    public void setOtherAccountId(Long otherAccountId) {
        this.otherAccountId = otherAccountId;
    }
}
