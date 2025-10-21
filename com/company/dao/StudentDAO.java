package com.company.dao;

import com.company.db.DatabaseManager;
import com.company.models.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations
 */
public class StudentDAO {
    
    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }
    
    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
    
    public Student getStudentByRoll(String roll) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_roll = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roll);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
    
    public List<Student> searchStudents(String query) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE first_name ILIKE ? OR last_name ILIKE ? OR student_roll = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, query);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }
    
    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students(first_name, last_name, student_roll, email, phone, class, division) VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getStudentRoll());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getStudentClass());
            stmt.setString(7, student.getDivision());
            stmt.executeUpdate();
        }
    }
    
    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, student_roll=?, email=?, phone=?, class=?, division=? WHERE student_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getStudentRoll());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPhone());
            stmt.setString(6, student.getStudentClass());
            stmt.setString(7, student.getDivision());
            stmt.setInt(8, student.getStudentId());
            stmt.executeUpdate();
        }
    }
    
    public void deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        }
    }
    
    public String getStudentRollByUserId(int userId) throws SQLException {
        String sql = "SELECT student_roll FROM students WHERE user_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("student_roll");
                }
            }
        }
        return null;
    }
    
    public int getStudentCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Student> getStudentsByClassAndDivision(String studentClass, String division) throws SQLException {
        List<Student> students = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        
        if (studentClass != null && !studentClass.isEmpty() && !studentClass.equals("All")) {
            sql.append(" AND class = ?");
        }
        if (division != null && !division.isEmpty() && !division.equals("All")) {
            sql.append(" AND division = ?");
        }
        sql.append(" ORDER BY class, division, student_id");
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (studentClass != null && !studentClass.isEmpty() && !studentClass.equals("All")) {
                stmt.setString(paramIndex++, studentClass);
            }
            if (division != null && !division.isEmpty() && !division.equals("All")) {
                stmt.setString(paramIndex++, division);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }
    
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setStudentRoll(rs.getString("student_roll"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setStudentClass(rs.getString("class"));
        student.setDivision(rs.getString("division"));
        
        int userId = rs.getInt("user_id");
        student.setUserId(rs.wasNull() ? null : userId);
        
        return student;
    }
}
