package com.company.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseManager {
    // Default PostgreSQL connection settings
    private static String URL = "jdbc:postgresql://localhost:5432/student_attendance_db";
    private static String USER = "postgres";
    private static String PASSWORD = "postgres";
    
    // Load database configuration from file if available
    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("db.properties");
            props.load(fis);
            URL = props.getProperty("db.url", URL);
            USER = props.getProperty("db.user", USER);
            PASSWORD = props.getProperty("db.password", PASSWORD);
            fis.close();
        } catch (IOException e) {
            System.out.println("Using default database configuration. Create db.properties file to customize.");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found.");
            e.printStackTrace();
            throw new SQLException("Database driver not found", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Getters for configuration (useful for setup/debugging)
    public static String getURL() {
        return URL;
    }
    
    public static String getUser() {
        return USER;
    }
}
