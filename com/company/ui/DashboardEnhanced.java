package com.company.ui;

import com.company.db.DatabaseManager;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.company.ui.UIConstants.*;

/**
 * Enhanced dashboard with charts and statistics
 */
public class DashboardEnhanced {
    
    /**
     * Create an enhanced admin dashboard with charts and statistics
     */
    public static JPanel createAdminDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Admin Dashboard", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Stats cards at top
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        StatsCard studentsCard = new StatsCard("Total Students", "0", "👥", PRIMARY_COLOR);
        StatsCard teachersCard = new StatsCard("Total Teachers", "0", "👨‍🏫", SUCCESS_COLOR);
        StatsCard subjectsCard = new StatsCard("Total Subjects", "0", "📚", WARNING_COLOR);
        StatsCard todayCard = new StatsCard("Today's Sessions", "0", "📅", ACCENT_COLOR);
        
        statsPanel.add(studentsCard);
        statsPanel.add(teachersCard);
        statsPanel.add(subjectsCard);
        statsPanel.add(todayCard);
        
        // Charts panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        chartsPanel.setBackground(BACKGROUND_COLOR);
        
        // Load data and create charts
        try {
            Map<String, Integer> attendanceData = loadAttendanceStats();
            Map<String, Integer> subjectData = loadSubjectAttendance();
            
            JPanel attendanceChart = ChartPanel.createPieChart("Today's Attendance", attendanceData);
            JPanel subjectChart = ChartPanel.createBarChart("Attendance by Subject (Last 7 Days)", 
                                                           subjectData, PRIMARY_COLOR);
            
            chartsPanel.add(attendanceChart);
            chartsPanel.add(subjectChart);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading charts: " + e.getMessage());
            errorLabel.setForeground(ERROR_COLOR);
            chartsPanel.add(errorLabel);
        }
        
        // Recent activity panel
        JPanel activityPanel = createRecentActivityPanel();
        
        // Combine charts and activity
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(chartsPanel, BorderLayout.CENTER);
        centerPanel.add(activityPanel, BorderLayout.SOUTH);
        
        // Assemble main panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load stats
        loadAdminStats(studentsCard, teachersCard, subjectsCard, todayCard);
        
        return mainPanel;
    }
    
    /**
     * Create enhanced teacher dashboard
     */
    public static JPanel createTeacherDashboard(int teacherId) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Teacher Dashboard", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        StatsCard sessionsCard = new StatsCard("My Sessions Today", "0", "📝", PRIMARY_COLOR);
        StatsCard avgAttendanceCard = new StatsCard("Avg Attendance", "0%", "📊", SUCCESS_COLOR);
        StatsCard studentsCard = new StatsCard("Total Students", "0", "👥", ACCENT_COLOR);
        
        statsPanel.add(sessionsCard);
        statsPanel.add(avgAttendanceCard);
        statsPanel.add(studentsCard);
        
        // Chart area
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(BACKGROUND_COLOR);
        try {
            Map<String, Integer> weekData = loadTeacherWeeklyAttendance(teacherId);
            JPanel chart = ChartPanel.createBarChart("Attendance This Week", weekData, PRIMARY_COLOR);
            chartPanel.add(chart, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading chart");
            errorLabel.setForeground(ERROR_COLOR);
            chartPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Load stats
        loadTeacherStats(teacherId, sessionsCard, avgAttendanceCard, studentsCard);
        
        return mainPanel;
    }
    
    /**
     * Create enhanced student dashboard
     */
    public static JPanel createStudentDashboard(int userId) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("My Attendance Dashboard", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        StatsCard overallCard = new StatsCard("Overall Attendance", "0%", "📊", PRIMARY_COLOR);
        StatsCard presentCard = new StatsCard("Present Days", "0", "✅", SUCCESS_COLOR);
        StatsCard absentCard = new StatsCard("Absent Days", "0", "❌", ERROR_COLOR);
        
        statsPanel.add(overallCard);
        statsPanel.add(presentCard);
        statsPanel.add(absentCard);
        
        // Progress by subject
        JPanel subjectPanel = createCardPanel();
        subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
        JLabel subjectTitle = createStyledLabel("Attendance by Subject", HEADER_FONT);
        subjectPanel.add(subjectTitle);
        subjectPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        try {
            Map<String, Double> subjectAttendance = loadStudentSubjectAttendance(userId);
            for (Map.Entry<String, Double> entry : subjectAttendance.entrySet()) {
                JPanel progressPanel = ChartPanel.createProgressBar(entry.getKey(), entry.getValue());
                subjectPanel.add(progressPanel);
                subjectPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading subject data");
            errorLabel.setForeground(ERROR_COLOR);
            subjectPanel.add(errorLabel);
        }
        
        // Scroll pane for subject progress
        JScrollPane scrollPane = new JScrollPane(subjectPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Load stats
        loadStudentStats(userId, overallCard, presentCard, absentCard);
        
        return mainPanel;
    }
    
    // Helper methods for loading data
    
    private static Map<String, Integer> loadAttendanceStats() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT status, COUNT(*) as count FROM attendance a " +
                        "JOIN sessions s ON a.session_id = s.session_id " +
                        "WHERE s.session_date = CURRENT_DATE " +
                        "GROUP BY status";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("status"), rs.getInt("count"));
                }
            }
        }
        return data;
    }
    
    private static Map<String, Integer> loadSubjectAttendance() throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT sb.subject_name, COUNT(*) as count FROM attendance a " +
                        "JOIN sessions s ON a.session_id = s.session_id " +
                        "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                        "WHERE s.session_date >= CURRENT_DATE - 7 AND a.status = 'Present' " +
                        "GROUP BY sb.subject_name ORDER BY count DESC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("subject_name"), rs.getInt("count"));
                }
            }
        }
        return data;
    }
    
    private static Map<String, Integer> loadTeacherWeeklyAttendance(int teacherId) throws SQLException {
        Map<String, Integer> data = new LinkedHashMap<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        
        try (Connection conn = DatabaseManager.getConnection()) {
            for (int i = 6; i >= 0; i--) {
                String sql = "SELECT COUNT(*) as count FROM attendance a " +
                            "JOIN sessions s ON a.session_id = s.session_id " +
                            "WHERE s.teacher_id = ? AND s.session_date = CURRENT_DATE - ? " +
                            "AND a.status = 'Present'";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, teacherId);
                    ps.setInt(2, i);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            data.put(days[(7 - i) % 7], rs.getInt("count"));
                        }
                    }
                }
            }
        }
        return data;
    }
    
    private static Map<String, Double> loadStudentSubjectAttendance(int userId) throws SQLException {
        Map<String, Double> data = new LinkedHashMap<>();
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT sb.subject_name, " +
                        "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) as percentage " +
                        "FROM attendance a " +
                        "JOIN students st ON a.student_id = st.student_id " +
                        "JOIN sessions s ON a.session_id = s.session_id " +
                        "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                        "WHERE st.user_id = ? " +
                        "GROUP BY sb.subject_name";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        data.put(rs.getString("subject_name"), rs.getDouble("percentage"));
                    }
                }
            }
        }
        return data;
    }
    
    private static void loadAdminStats(StatsCard students, StatsCard teachers, 
                                      StatsCard subjects, StatsCard today) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Students count
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM students")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) students.setValue(String.valueOf(rs.getInt(1)));
            }
            
            // Teachers count
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM teachers")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) teachers.setValue(String.valueOf(rs.getInt(1)));
            }
            
            // Subjects count
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM subjects")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) subjects.setValue(String.valueOf(rs.getInt(1)));
            }
            
            // Today's sessions
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM sessions WHERE session_date = CURRENT_DATE")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) today.setValue(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadTeacherStats(int teacherId, StatsCard sessions, 
                                        StatsCard avgAttendance, StatsCard students) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Today's sessions
            String sql = "SELECT COUNT(*) FROM sessions WHERE teacher_id = ? AND session_date = CURRENT_DATE";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, teacherId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) sessions.setValue(String.valueOf(rs.getInt(1)));
            }
            
            // Average attendance
            sql = "SELECT AVG(CASE WHEN a.status = 'Present' THEN 1.0 ELSE 0.0 END) * 100 as avg " +
                  "FROM attendance a JOIN sessions s ON a.session_id = s.session_id " +
                  "WHERE s.teacher_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, teacherId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    avgAttendance.setValue(String.format("%.1f%%", rs.getDouble("avg")));
                }
            }
            
            // Total students
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM students")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) students.setValue(String.valueOf(rs.getInt(1)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void loadStudentStats(int userId, StatsCard overall, 
                                        StatsCard present, StatsCard absent) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT " +
                        "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) as present_count, " +
                        "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) as absent_count, " +
                        "COUNT(*) as total " +
                        "FROM attendance a " +
                        "JOIN students st ON a.student_id = st.student_id " +
                        "WHERE st.user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int presentCount = rs.getInt("present_count");
                    int absentCount = rs.getInt("absent_count");
                    int total = rs.getInt("total");
                    
                    present.setValue(String.valueOf(presentCount));
                    absent.setValue(String.valueOf(absentCount));
                    
                    if (total > 0) {
                        double percentage = (presentCount * 100.0) / total;
                        overall.setValue(String.format("%.1f%%", percentage));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static JPanel createRecentActivityPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 200));
        
        JLabel title = createStyledLabel("Recent Activity", HEADER_FONT);
        panel.add(title, BorderLayout.NORTH);
        
        JTextArea activityArea = new JTextArea();
        activityArea.setEditable(false);
        activityArea.setFont(SMALL_FONT);
        activityArea.setBackground(CARD_BACKGROUND);
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT s.session_date, sb.subject_name, COUNT(*) as count " +
                        "FROM sessions s JOIN subjects sb ON s.subject_id = sb.subject_id " +
                        "LEFT JOIN attendance a ON a.session_id = s.session_id " +
                        "WHERE s.session_date >= CURRENT_DATE - 7 " +
                        "GROUP BY s.session_id, s.session_date, sb.subject_name " +
                        "ORDER BY s.session_date DESC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                StringBuilder sb = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    sb.append("• ")
                      .append(sdf.format(rs.getDate("session_date")))
                      .append(" - ")
                      .append(rs.getString("subject_name"))
                      .append(" (")
                      .append(rs.getInt("count"))
                      .append(" students)\n");
                }
                activityArea.setText(sb.toString());
            }
        } catch (SQLException e) {
            activityArea.setText("Error loading activity");
        }
        
        JScrollPane scrollPane = new JScrollPane(activityArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Custom stats card component
     */
    static class StatsCard extends JPanel {
        private JLabel valueLabel;
        
        public StatsCard(String title, String value, String icon, Color color) {
            setLayout(new BorderLayout(10, 10));
            setBackground(CARD_BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            // Icon and title
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            
            JLabel titleLabel = createStyledLabel(title, SMALL_FONT);
            titleLabel.setForeground(TEXT_SECONDARY);
            
            topPanel.add(iconLabel, BorderLayout.WEST);
            topPanel.add(titleLabel, BorderLayout.CENTER);
            
            // Value
            valueLabel = createStyledLabel(value, new Font("Segoe UI", Font.BOLD, 32));
            valueLabel.setForeground(color);
            
            add(topPanel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }
        
        public void setValue(String value) {
            valueLabel.setText(value);
        }
    }
}
