package com.iut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.iut.account.model.Account;
import com.iut.account.service.AccountService;

public class AccountServiceTest {

    private Repository<Account, String> repository;
    private AccountService accountService;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(Repository.class);
        accountService = new AccountService(repository);
    }

    @Test
    void createAccountTest() {
        String accountId1 = "acc001";
        String accountId2 = "acc002";
        String userId = "user123";

        // @ Scenario 1: Account does not exist
        when(repository.existsById(accountId1)).thenReturn(false);
        when(repository.save(any(Account.class))).thenReturn(true);

        boolean result1 = accountService.createAccount(accountId1, 1000, userId);

        assertTrue(result1, "Account should be created when ID does not exist");
        verify(repository).existsById(accountId1);
        verify(repository).save(argThat(account -> account.getId().equals(accountId1) &&
                account.getBalance() == 1000 &&
                userId.equals(account.getUserId())));

        // @ Scenario 2: Account already exists
        when(repository.existsById(accountId2)).thenReturn(true);

        boolean result2 = accountService.createAccount(accountId2, 2000, userId);

        assertFalse(result2, "Account should not be created when ID already exists");
        verify(repository).existsById(accountId2);
        verify(repository, never()).save(argThat(account -> account.getId().equals(accountId2)));
    }

    @Test
    void depositTest() {
        String accountId1 = "acc001";
        String accountId2 = "acc002";
        int initialBalance = 500;
        int depositAmount = 300;

        Account account = new Account(accountId1, initialBalance);

        // @ Scenario 1: Account exists, deposit should succeed
        when(repository.existsById(accountId1)).thenReturn(true);
        when(repository.findById(accountId1)).thenReturn(account);
        when(repository.update(any(Account.class))).thenReturn(true);

        boolean result1 = accountService.deposit(accountId1, depositAmount);

        assertTrue(result1, "Deposit should succeed when account exists");
        assertEquals(initialBalance + depositAmount, account.getBalance());
        verify(repository).existsById(accountId1);
        verify(repository).findById(accountId1);
        verify(repository).update(account);

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 2: Account does not exist, deposit should fail
        when(repository.existsById(accountId2)).thenReturn(false);

        boolean result2 = accountService.deposit(accountId2, depositAmount);

        assertFalse(result2, "Deposit should fail when account does not exist");
        verify(repository).existsById(accountId2);
        verify(repository, never()).findById(accountId2);
        verify(repository, never()).update(any());
    }

    @Test
    void withdrawTest() {
        String accountId1 = "acc001";
        String accountId2 = "acc002";
        String accountId3 = "acc003";
        int initialBalance = 1000;
        int withdrawAmount = 400;
        int overWithdrawAmount = 2000;

        // @ Scenario 1: Success
        Account acc1 = new Account(accountId1, initialBalance);
        when(repository.existsById(accountId1)).thenReturn(true);
        when(repository.findById(accountId1)).thenReturn(acc1);
        when(repository.update(acc1)).thenReturn(true);

        boolean result1 = accountService.withdraw(accountId1, withdrawAmount);

        assertTrue(result1, "Withdraw should succeed when balance is sufficient");
        assertEquals(initialBalance - withdrawAmount, acc1.getBalance());

        verify(repository).existsById(accountId1);
        verify(repository).findById(accountId1);
        verify(repository).update(acc1);

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 2: Insufficient funds
        Account acc2 = new Account(accountId2, initialBalance);
        when(repository.existsById(accountId2)).thenReturn(true);
        when(repository.findById(accountId2)).thenReturn(acc2);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(accountId2, overWithdrawAmount));
        assertEquals("Insufficient funds", exception.getMessage());

        verify(repository).existsById(accountId2);
        verify(repository).findById(accountId2);
        verify(repository, never()).update(any());

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 3: Account does not exist
        when(repository.existsById(accountId3)).thenReturn(false);
        boolean result3 = accountService.withdraw(accountId3, withdrawAmount);

        assertFalse(result3, "Withdraw should fail when account doesn't exist");

        verify(repository).existsById(accountId3);
        verify(repository, never()).findById(accountId3);
        verify(repository, never()).update(any());
    }

    @Test
    void transferTest() {
        String fromId1 = "from1";
        String toId1 = "to1";
        String fromId2 = "from2";
        String toId2 = "to2";
        String fromId3 = "from3";
        String toId3 = "to3";
        int initialBalance = 1000;
        int transferAmount = 300;
        int overTransfer = 2000;

        // @ Scenario 1: Success
        Account fromAcc1 = new Account(fromId1, initialBalance);
        Account toAcc1 = new Account(toId1, 500);

        when(repository.existsById(fromId1)).thenReturn(true);
        when(repository.existsById(toId1)).thenReturn(true);
        when(repository.findById(fromId1)).thenReturn(fromAcc1);
        when(repository.findById(toId1)).thenReturn(toAcc1);
        when(repository.update(fromAcc1)).thenReturn(true);
        when(repository.update(toAcc1)).thenReturn(true);

        boolean result1 = accountService.transfer(fromId1, toId1, transferAmount);

        assertTrue(result1, "Transfer should succeed when both accounts exist and funds are sufficient");
        assertEquals(initialBalance - transferAmount, fromAcc1.getBalance());
        assertEquals(800, toAcc1.getBalance());

        verify(repository).update(fromAcc1);
        verify(repository).update(toAcc1);

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 2: Insufficient Funds
        Account fromAcc2 = new Account(fromId2, initialBalance);
        Account toAcc2 = new Account(toId2, 0);

        when(repository.existsById(fromId2)).thenReturn(true);
        when(repository.existsById(toId2)).thenReturn(true);
        when(repository.findById(fromId2)).thenReturn(fromAcc2);
        when(repository.findById(toId2)).thenReturn(toAcc2);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.transfer(fromId2, toId2, overTransfer));
        assertEquals("Insufficient funds in source account", exception.getMessage());

        verify(repository, never()).update(any());

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 3: Source account doesn't exist
        when(repository.existsById(fromId3)).thenReturn(false);
        when(repository.existsById(toId3)).thenReturn(true);

        boolean result3 = accountService.transfer(fromId3, toId3, transferAmount);
        assertFalse(result3, "Transfer should fail when source account doesn't exist");

        verify(repository).existsById(fromId3);
        verify(repository, never()).existsById(toId3);

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 4: Destination account doesn't exist
        when(repository.existsById(fromId3)).thenReturn(true);
        when(repository.existsById(toId3)).thenReturn(false);

        boolean result4 = accountService.transfer(fromId3, toId3, transferAmount);
        assertFalse(result4, "Transfer should fail when destination account doesn't exist");

        verify(repository).existsById(fromId3);
        verify(repository).existsById(toId3);
    }

    @Test
    void getBalanceTest() {
        String id1 = "acc1";
        String id2 = "acc2";
        int balance = 750;

        // @ Scenario 1: Account exists
        Account account = new Account(id1, balance);
        when(repository.existsById(id1)).thenReturn(true);
        when(repository.findById(id1)).thenReturn(account);

        int result = accountService.getBalance(id1);
        assertEquals(balance, result, "Should return the correct account balance");

        verify(repository).existsById(id1);
        verify(repository).findById(id1);

        // Reset mock before next scenario
        reset(repository);

        // @ Scenario 2: Account does not exist
        when(repository.existsById(id2)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.getBalance(id2));
        assertEquals("User not found", exception.getMessage());

        verify(repository).existsById(id2);
        verify(repository, never()).findById(id2);
    }

    @Test
    void existsAndGetAccountTest() {
        String id1 = "acc1";
        String id2 = "acc2";

        Account account = new Account(id1, 500);
        when(repository.findById(id1)).thenReturn(account);
        when(repository.findById(id2)).thenReturn(null);

        // @ Scenario 1: Account exists
        Account result1 = accountService.getAccount(id1);
        assertNotNull(result1);
        assertEquals(id1, result1.getId());
        assertEquals(500, result1.getBalance());

        // @ Scenario 2: Account does not exist
        Account result2 = accountService.getAccount(id2);
        assertNull(result2);

        verify(repository).findById(id1);
        verify(repository).findById(id2);
    }

    @Test
    void getAllAccountsTest() {
        List<Account> mockAccounts = List.of(
                new Account("acc1", 100),
                new Account("acc2", 200),
                new Account("acc3", 300));

        when(repository.findAll()).thenReturn(mockAccounts);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("acc1", result.get(0).getId());
        assertEquals(100, result.get(0).getBalance());

        verify(repository).findAll();
    }
}
