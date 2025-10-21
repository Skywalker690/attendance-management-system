package com.company.dao;

import com.company.db.DatabaseManager;
import com.company.models.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Teacher operations
 */
public class TeacherDAO {
    
    public List<Teacher> getAllTeachers() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers ORDER BY teacher_id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                teachers.add(mapResultSetToTeacher(rs));
            }
        }
        return teachers;
    }
    
    public Teacher getTeacherById(int teacherId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacher(rs);
                }
            }
        }
        return null;
    }
    
    public void addTeacher(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teachers(first_name, last_name, email, phone) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, teacher.getFirstName());
            stmt.setString(2, teacher.getLastName());
            stmt.setString(3, teacher.getEmail());
            stmt.setString(4, teacher.getPhone());
            stmt.executeUpdate();
        }
    }
    
    public void updateTeacher(Teacher teacher) throws SQLException {
        String sql = "UPDATE teachers SET first_name=?, last_name=?, email=?, phone=? WHERE teacher_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, teacher.getFirstName());
            stmt.setString(2, teacher.getLastName());
            stmt.setString(3, teacher.getEmail());
            stmt.setString(4, teacher.getPhone());
            stmt.setInt(5, teacher.getTeacherId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteTeacher(int teacherId) throws SQLException {
        String sql = "DELETE FROM teachers WHERE teacher_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            stmt.executeUpdate();
        }
    }
    
    public int getTeacherCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(rs.getInt("teacher_id"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setEmail(rs.getString("email"));
        teacher.setPhone(rs.getString("phone"));
        
        int userId = rs.getInt("user_id");
        teacher.setUserId(rs.wasNull() ? null : userId);
        
        return teacher;
    }
}
