package com.iut;

import java.sql.*;

public class Database {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
        }
        return connection;
    }
}
