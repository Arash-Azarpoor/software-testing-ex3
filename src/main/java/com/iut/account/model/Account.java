package com.iut.account.model;

public class Account {

    private String id;
    private int balance;
    private String userId;

    public Account(int balance) {
        this.balance = balance;
    }

    public Account(String id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(String id, int balance, String userId) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public String getUserId() {
        return userId;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
