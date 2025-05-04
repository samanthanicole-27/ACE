package ace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "#paranisacs27";
    private static final Map<String, Connection> connections = new HashMap<>();

    private static Connection getConnection(String dbName) throws SQLException, ClassNotFoundException { // Added exceptions
        if (connections.containsKey(dbName) && !connections.get(dbName).isClosed()) {
            return connections.get(dbName);
        }
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = URL + dbName;
        Connection con = DriverManager.getConnection(url, USER, PASSWORD);
        connections.put(dbName, con);
        return con;
    }

    public static Connection getUserAccountsDatabaseConnection() throws SQLException, ClassNotFoundException {
        return getConnection("user_accounts");
    }

    public static Connection getAceDatabaseConnection() throws SQLException, ClassNotFoundException {
        return getConnection("ace");
    }

    /**
     * Register a new user
     *
     * @param email    User's PSHS-ZRC email
     * @param name     User's name
     * @param password User's password
     * @param username User's username
     * @param age      User's age
     * @return the generated user ID if registration is successful, -1 otherwise
     */
    public int registerUser(String email, String username, String password, String name, String age) {
        String query = "INSERT INTO users (email, username, password, name, age) VALUES (?, ?, ?, ?, ?)";
        String hashedPassword = hashPassword(password);
        try (Connection connection = getAceDatabaseConnection(); // Get connection here
             PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, email);
            pst.setString(2, username);
            pst.setString(3, hashedPassword);
            pst.setString(4, name);
            pst.setString(5, age);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException | ClassNotFoundException e) { // Catch both exceptions
            System.err.println("Error registering user: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database error during registration: " + e.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return -1;
    }

    /**
     * Authenticate user login
     *
     * @param usernameOrEmail Username or email
     * @param password        Password
     * @return true if authentication successful, false otherwise
     */
    public boolean loginUser(String usernameOrEmail, String password) {
        String query = "SELECT password FROM users WHERE (username = ? OR email = ?)";
        try (Connection connection = getAceDatabaseConnection(); // Get connection here
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, usernameOrEmail);
            statement.setString(2, usernameOrEmail);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return validatePassword(password, storedPassword);
            }
        } catch (SQLException | ClassNotFoundException e) { // Catch both exceptions
            System.err.println("Error authenticating user: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error during login: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public int getid(String emailOrUsername) {
        String query = "SELECT user_id FROM users WHERE (email = ? OR username = ?)";
        try (Connection connection = getAceDatabaseConnection(); // Get connection here
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException | ClassNotFoundException e) { // Catch both exceptions.
            System.err.println("Error retrieving user ID: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error retrieving user ID: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
        return -1;
    }

    /**
     * Check if email already exists in the database
     *
     * @param email Email to check
     * @return true if the email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = getAceDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException | ClassNotFoundException e) { // Catch both exceptions
            System.err.println("Error checking email existence: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Email Check Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }
    

    /**
     * Check if the username already exists in the database
     *
     * @param username Username to check
     * @return true if the username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection connection = getAceDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException | ClassNotFoundException e) { // Catch both exceptions
            System.err.println("Error checking username existence: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "DB error: " + e.getMessage(), "Username Check Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }

    /**
     * Hash the password using SHA-256
     *
     * @param password Password to hash
     * @return Hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error hashing password: " + e.getMessage(), "Hashing Error", JOptionPane.ERROR_MESSAGE);
            return password;
        }
    }

    /**
     * Validate the password against the stored hash
     *
     * @param inputPassword  Input password
     * @param storedPassword Stored hashed password
     * @return true if the password matches, false otherwise
     */
    private boolean validatePassword(String inputPassword, String storedPassword) {
        return hashPassword(inputPassword).equals(storedPassword);
    }
}


