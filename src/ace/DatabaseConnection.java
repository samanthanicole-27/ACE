package ace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {

    // Store database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root"; // Your MySQL username
    private static final String PASSWORD = "#paranisacs27"; // Your MySQL password

    // Use a map to store connections for different databases
    private static final Map<String, Connection> connections = new HashMap<>();

    // Method to get connection to a database
    public static Connection getConnection(String dbName) {
        // Check if a connection already exists for this database
        if (connections.containsKey(dbName)) {
            return connections.get(dbName);
        }

        Connection con = null;
        try {
            // Load JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connection URL uses the provided database name
            String url = URL + dbName;

            // Establish connection
            con = DriverManager.getConnection(url, USER, PASSWORD);

            // Store the connection in the map
            connections.put(dbName, con);
        } catch (Exception e) {
            e.printStackTrace(); // Log any error
        }
        return con;
    }

    // Method to get connection to the 'user_accounts' database
    public static Connection getUserAccountsDatabaseConnection() {
        return getConnection("user_accounts");
    }

    // Method to get connection to the 'ace' database
    public static Connection getAceDatabaseConnection() {
        return getConnection("ace");
    }

    // Optional: Test connections
    public static void main(String[] args) {
        // Test connection to 'user_accounts' database
        Connection userAccountsConn = null;
        try {
            userAccountsConn = getUserAccountsDatabaseConnection();
            if (userAccountsConn != null) {
                System.out.println("Connected to 'user_accounts' database successfully!");
            } else {
                System.out.println("Connection to 'user_accounts' database failed.");
            }
        } finally {
            // Close connection if it was opened
            if (userAccountsConn != null) {
                try {
                    userAccountsConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Test connection to 'ace' database
        Connection aceConn = null;
        try {
            aceConn = getAceDatabaseConnection();
            if (aceConn != null) {
                System.out.println("Connected to 'ace' database successfully!");
            } else {
                System.out.println("Connection to 'ace' database failed.");
            }
        } finally {
            // Close connection if it was opened
            if (aceConn != null) {
                try {
                    aceConn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
