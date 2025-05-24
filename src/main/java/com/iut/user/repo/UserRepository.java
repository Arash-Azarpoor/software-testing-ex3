package com.iut.user.repo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.iut.Database;
import com.iut.Repository;
import com.iut.user.model.User;

public class UserRepository implements Repository<User, String> {

    private Connection connection;

    public UserRepository() throws SQLException {
        connection = Database.getConnection();
        connection.createStatement().execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR PRIMARY KEY, name VARCHAR, last_name VARCHAR, age INT)");
    }

    @Override
    public boolean save(User input) {
        String insertSQL = "INSERT INTO users (id, name, last_name, age) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setString(1, input.getId());
            pstmt.setString(2, input.getName());
            pstmt.setString(3, input.getLastName());
            pstmt.setInt(4, input.getAge());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User input) {
        String updateSQL = "UPDATE users SET name = ?, last_name = ?, age = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateSQL)) {
            pstmt.setString(1, input.getName());
            pstmt.setString(2, input.getLastName());
            pstmt.setInt(3, input.getAge());
            pstmt.setString(4, input.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }        
    }

    @Override
    public List<User> findAll() {
        String selectSQL = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                users.add(new User(id, name, lastName, age));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return users;
    }
    
    @Override
    public boolean existsById(String id) {
        String querySQL = "SELECT COUNT(*) FROM users WHERE id = ?";
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
    public User findById(String id) {
        String selectSQL = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getInt("age"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(String id) {
        String deleteSQL = "DELETE FROM users WHERE id = ?";
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
