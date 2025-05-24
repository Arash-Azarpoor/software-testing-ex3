package com.iut.user.model;

import java.util.ArrayList;
import java.util.List;
import com.iut.account.model.Account;

public class User {

    private final String id;
    private String name;
    private String lastName;
    private int age;
    private final List<Account> accounts = new ArrayList<>();

    public User(String id) {
        this.id = id;
    }

    public User(String id, String name, String lastName, int age) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}
