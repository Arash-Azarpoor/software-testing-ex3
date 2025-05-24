package com.iut.account.repo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.iut.Database;
import com.iut.Repository;
import com.iut.account.model.Account;

public class AccountRepository implements Repository<Account, String> {

    private Connection connection;

    public AccountRepository() throws SQLException {
        connection = Database.getConnection();
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS bank_account ("
                + "id VARCHAR PRIMARY KEY,"
                + "balance INT,"
                + "user_id VARCHAR,"
                + "FOREIGN KEY (user_id) REFERENCES users(id)"
                + ");");
    }

    @Override
    public boolean save(Account input) {
        String insertSQL = "INSERT INTO bank_account (id, balance, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, input.getId());
            pstmt.setInt(2, input.getBalance());
            pstmt.setString(3, input.getUserId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Account input) {
        String updateSQL = "UPDATE bank_account SET balance = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setInt(1, input.getBalance());
            pstmt.setString(2, input.getUserId());
            pstmt.setString(3, input.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existsById(String id) {
        String querySQL = "SELECT COUNT(*) FROM bank_account WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Account findById(String id) {
        String selectSQL = "SELECT * FROM bank_account WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getString("id"),
                        rs.getInt("balance"),
                        rs.getString("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Account> findAll() {
        String selectSQL = "SELECT * FROM bank_account";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                accounts.add(new Account(
                        rs.getString("id"),
                        rs.getInt("balance"),
                        rs.getString("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    @Override
    public boolean delete(String id) {
        String deleteSQL = "DELETE FROM bank_account WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
