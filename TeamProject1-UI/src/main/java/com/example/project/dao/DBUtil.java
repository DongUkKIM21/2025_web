package com.example.project.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    public static void init(String driver, String url, String user, String password) {
        DBUtil.driver = driver;
        DBUtil.url = url;
        DBUtil.user = user;
        DBUtil.password = password;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found.", e);
        }
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    // Log or ignore
                    e.printStackTrace();
                }
            }
        }
    }
} 