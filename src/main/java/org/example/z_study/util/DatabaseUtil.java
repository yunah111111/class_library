package org.example.z_study.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/library?serverTimeZone=Asia/Seoul";
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        return connection;
    }

}
