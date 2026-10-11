package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager for MySQL database.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/real_estate_mandate_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "YOUR_PASSWORD";

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    /**
     * Retrieves the singleton database connection.
     * Reopens or creates a new connection if it is null or closed.
     *
     * @return Connection instance, or null if connection fails
     */
    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure driver is loaded
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("[DatabaseConnection] MySQL JDBC Driver not found: " + e.getMessage());
                }

                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("[DatabaseConnection] Connection established successfully to " + URL);
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Failed to connect to database: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Closes the active database connection if open.
     */
    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
                System.out.println("[DatabaseConnection] Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error closing database connection: " + e.getMessage());
        }
    }
}
