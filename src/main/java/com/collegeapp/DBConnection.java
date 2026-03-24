package com.collegeapp;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection — Singleton class for managing the JDBC database connection.
 *
 * Only one Connection instance exists for the entire application lifetime.
 * All DAOs call DBConnection.getConnection() to get the shared connection.
 *
 * Reads credentials from src/main/resources/config.properties — never
 * hardcoded in source code.
 *
 * Developer: Pranav Dadhe
 * Package: com.collegeapp
 */
public class DBConnection {

    // The single shared connection instance — null until first call
    private static Connection connection = null;

    // Private constructor — prevents anyone from doing new DBConnection()
    private DBConnection() {
    }

    /**
     * Returns the single shared database connection.
     * Creates it on first call, reuses it on every subsequent call.
     *
     * @return java.sql.Connection — active connection to campusdb
     * @throws RuntimeException if config.properties is missing or DB is unreachable
     */
    public static Connection getConnection() {

        try {
            // If connection doesn't exist yet OR was closed — create a new one
            if (connection == null || connection.isClosed()) {
                connection = createConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check connection status: "
                    + e.getMessage(), e);
        }

        return connection;
    }

    /**
     * Reads config.properties and creates a new JDBC Connection.
     * Called only when connection is null or closed.
     *
     * @return a fresh Connection to campusdb
     * @throws RuntimeException if properties file is missing or connection fails
     */
    private static Connection createConnection() {

        // Step 1 — Load config.properties from the classpath (resources folder)
        Properties props = new Properties();

        try (InputStream input = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            // If file not found, getResourceAsStream returns null
            if (input == null) {
                throw new RuntimeException(
                        "config.properties not found in src/main/resources. " +
                                "Copy config.properties.example and fill in your credentials.");
            }

            // Load all key=value pairs from the file
            props.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read config.properties: " + e.getMessage(), e);
        }

        // Step 2 — Extract the three required values
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        String driver = props.getProperty("jdbc.driver");

        // Step 3 — Validate nothing is missing
        if (url == null || username == null || password == null || driver == null) {
            throw new RuntimeException(
                    "config.properties is missing one or more required keys: " +
                            "jdbc.url, jdbc.username, jdbc.password, jdbc.driver");
        }

        // Step 4 — Load the MySQL JDBC driver class
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "MySQL JDBC driver not found. Check pom.xml dependencies: "
                            + e.getMessage(),
                    e);
        }

        // Step 5 — Create and return the actual connection
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("[DBConnection] Connected to campusdb successfully.");
            return conn;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to connect to MySQL. Check config.properties and " +
                            "ensure MySQL server is running: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Closes the connection when the application shuts down.
     * Call this from MainFrame when the app exits.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: "
                        + e.getMessage());
            }
        }
    }
}
