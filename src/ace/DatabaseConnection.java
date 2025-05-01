package ace;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load JDBC driver for MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Database connection details
            String url = "jdbc:mysql://localhost:3306/ace"; // replace 'ace' with your database name if needed
            String user = "root";                            // your MySQL username
            String password = "#paranisacs27";              // your MySQL password

            // Establish connection
            con = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace(); // Log any error
        }
        return con;
    }

    // Optional: Test connection via main
    public static void main(String[] args) {
        Connection testConnection = getConnection();
        if (testConnection != null) {
            System.out.println("Connection successful!");
            try {
                testConnection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection failed.");
        }
    }
}
