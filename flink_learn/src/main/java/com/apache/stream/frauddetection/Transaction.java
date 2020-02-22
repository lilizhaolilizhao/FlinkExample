package com.apache.stream.frauddetection;

public class Transaction {
    private String accountId;
    private double amount;

    public Transaction() {
    }

    public Transaction(String accountId, double amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accountId='" + accountId + '\'' +
                ", amount=" + amount +
                '}';
    }
}
