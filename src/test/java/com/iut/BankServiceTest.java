package com.iut;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.iut.account.model.Account;
import com.iut.account.repo.AccountRepository;
import com.iut.account.service.AccountService;
import com.iut.user.model.User;
import com.iut.user.repo.UserRepository;
import com.iut.user.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTest {

    private static UserRepository userRepository;
    private static UserService userService;
    private static AccountRepository accountRepository;
    private static AccountService accountService;
    private static BankService bankService;

    final String userId = "1234";

    @BeforeAll
    static void generateDatabase() throws SQLException {
        userRepository = new UserRepository();
        accountRepository = new AccountRepository();
        userService = new UserService(userRepository);
        accountService = new AccountService(accountRepository);
        bankService = new BankService(userService, accountService);
    }

    @Order(1)
    @Test
    void registerNewUserTest() {
        User user = new User(userId, "ali", "ahmadi", 24);
        Assertions.assertTrue(bankService.registerNewUser(user));
    }

    @Order(2)
    @Test
    void defaultAccountForNewUserTest() {
        List<Account> accounts = bankService.getUserAccounts(userId);
        Assertions.assertEquals(1, accounts.size());
        Assertions.assertEquals(0, accounts.get(0).getBalance());
    }

    @Order(3)
    @Test
    void getUserTest() {
        User user = bankService.getUser(userId);
        Assertions.assertEquals("ahmadi", user.getLastName());
    }

    @Order(4)
    @Test
    void addAccountToUserTest() {
        Account account = new Account("2121926617997306", 2000);
        Assertions.assertTrue(bankService.addAccountToUser(userId, account));
    }

    @Order(5)
    @Test
    void getNewAccountOfUserTest() {
        List<Account> accounts = bankService.getUserAccounts(userId);
        Assertions.assertEquals(2, accounts.size());
        final String accountId = accounts.get(1).getId();

        Assertions.assertEquals(2000,
                bankService.getAccount(accountId).getBalance());
    }

    @Order(6)
    @Test
    void deleteAccountTest() {
        List<Account> accounts = bankService.getUserAccounts(userId);
        Assertions.assertTrue(bankService.deleteAccount(accounts.get(0).getId()));

        List<Account> accountsAfterDelete = bankService.getUserAccounts(userId);
        Assertions.assertEquals(1, accountsAfterDelete.size());
    }
}
