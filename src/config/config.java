package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;
import net.proteanit.sql.DbUtils;

public class config {
    public static int loggedInAID;

    // Connect to SQLite
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:sample.db");
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return con;
    }

    // Add a record safely
    public void addRecord(String sql, Object... values) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // Authenticate user/admin and return role
    public String authenticate(String usernameOrEmail, String password) {
        String role = null;
        
        String sql = "SELECT u_id, role, status FROM tble_user WHERE (email = ? OR name = ?) AND password  = ? AND status = ?";
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usernameOrEmail);
            pstmt.setString(2, usernameOrEmail);
            pstmt.setString(3, password);
            pstmt.setString(4, "Active");
            
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loggedInAID = rs.getInt("u_id");

                    role = rs.getString("role"); // returns "admin" or "user"
                }
            }

        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
        }

        return role;
    }

    // Display data in a JTable
    public void displayData(String sql, JTable usertable) {
        try (Connection conn = connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            usertable.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (SQLException e) {
            System.out.println("Error displaying data: " + e.getMessage());
        }
    }

    public String authenticate(String sql, String text, String text0, String active) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
