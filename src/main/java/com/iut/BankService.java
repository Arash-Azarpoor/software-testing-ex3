package com.iut;

import java.util.List;
import java.util.stream.Collectors;

import com.iut.account.model.Account;
import com.iut.account.service.AccountService;
import com.iut.user.model.User;
import com.iut.user.service.UserService;

public class BankService {
    private final UserService userService;
    private final AccountService accountService;

    public BankService(final UserService userService, final AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    public boolean registerNewUser(User user) {
        boolean userCreated = userService.createUser(user);
        if (!userCreated) {
            return false;
        }

        String defaultAccountId = "default_" + user.getId();
        boolean accountCreated = accountService.createAccount(defaultAccountId, 0, user.getId());

        if (!accountCreated) {
            return false;
        }

        return true;
    }

    public List<Account> getUserAccounts(String userId) {
        return accountService.getAllAccounts().stream()
                .filter(account -> account != null && userId.equals(account.getUserId()))
                .collect(Collectors.toList());
    }

    public User getUser(String userId) {
        return userService.getUser(userId);
    }

    public boolean addAccountToUser(String userId, Account account) {
        User user = userService.getUser(userId);

        if (user == null) {
            return false;
        }

        if (accountService.getAccount(account.getId()) != null) {
            return false;
        }

        account.setUserId(userId);
        return accountService.createAccount(account.getId(), account.getBalance(), userId);
    }

    public Account getAccount(String accountId) {
        return accountService.getAccount(accountId);
    }

    public boolean deleteAccount(String accountId) {
        return accountService.deleteAccount(accountId);
    }
}
