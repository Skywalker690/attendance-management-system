package com.company.dao;

import com.company.db.DatabaseManager;
import com.company.models.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Subject operations
 */
public class SubjectDAO {
    
    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_id";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }
    
    public Subject getSubjectById(int subjectId) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, subjectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubject(rs);
                }
            }
        }
        return null;
    }
    
    public Subject getSubjectByName(String subjectName) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE subject_name = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, subjectName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubject(rs);
                }
            }
        }
        return null;
    }
    
    public void addSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects(subject_name, subject_code) VALUES(?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getSubjectCode());
            stmt.executeUpdate();
        }
    }
    
    public void deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE subject_id=?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, subjectId);
            stmt.executeUpdate();
        }
    }
    
    public int getSubjectCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setSubjectId(rs.getInt("subject_id"));
        subject.setSubjectName(rs.getString("subject_name"));
        subject.setSubjectCode(rs.getString("subject_code"));
        subject.setDescription(rs.getString("description"));
        return subject;
    }
}
