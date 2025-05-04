package ace;

import static ace.DatabaseConnection.getAceDatabaseConnection;
import java.sql.Connection;
import java.util.ArrayList;

public class GradeDAO {
    private DatabaseConnection dbConnection;
    
    public GradeDAO() {
        this.dbConnection = new DatabaseConnection();
    }
    
    /**
     * Fetches grades for a specific user from the database
     * @param userId The ID of the user whose grades to retrieve
     * @return ArrayList of grades for the user
     */
    public ArrayList<Grade> getGradesByUserId(String userId) {
        ArrayList<Grade> grades = new ArrayList<>();
        Connection aceConn = getAceDatabaseConnection();
        
        try {
            // Modify this query to match your actual grades table structure
            String query = "SELECT * FROM grades WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                // Create Grade object and set properties from ResultSet
                // Modify these to match your actual grade attributes
                Grade grade = new Grade();
                grade.setId(rs.getString("grade_id"));
                grade.setUserId(rs.getString("user_id"));
                grade.setSubject(rs.getString("subject"));
                grade.setScore(rs.getDouble("score"));
                
                grades.add(grade);
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return grades;
    }
    
    /**
     * Adds a new grade for a user
     * @param grade The grade to add
     * @return true if successful, false otherwise
     */
    public boolean addGrade(Grade grade) {
        Connection conn = dbConnection.getConnection();
        
        try {
            // Modify this query to match your grades table structure
            String query = "INSERT INTO grades (user_id, subject, score) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, grade.getUserId());
            pstmt.setString(2, grade.getSubject());
            pstmt.setDouble(3, grade.getScore());
            
            int result = pstmt.executeUpdate();
            pstmt.close();
            
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
