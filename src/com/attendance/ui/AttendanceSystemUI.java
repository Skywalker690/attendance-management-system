package com.attendance.ui;

import com.attendance.manager.AttendanceManager;
import com.attendance.model.Student;
import com.attendance.model.AttendanceRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main UI class for Student Attendance Management System
 */
public class AttendanceSystemUI extends JFrame {
    
    private AttendanceManager attendanceManager;
    private JTabbedPane tabbedPane;
    
    // Color scheme for modern UI
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_COLOR = Color.WHITE;
    
    public AttendanceSystemUI() {
        attendanceManager = new AttendanceManager();
        initializeUI();
        addSampleData(); // Add some sample data for demonstration
    }
    
    private void initializeUI() {
        setTitle("Student Attendance Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed pane for different sections
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(PANEL_COLOR);
        
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Student Management", createStudentManagementPanel());
        tabbedPane.addTab("Mark Attendance", createAttendanceMarkingPanel());
        tabbedPane.addTab("Reports", createReportsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Student Attendance Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        // Total students card
        JPanel totalStudentsCard = createStatCard("Total Students", "0", PRIMARY_COLOR);
        statsPanel.add(totalStudentsCard);
        
        // Today's attendance card
        JPanel todayAttendanceCard = createStatCard("Today's Attendance", "0%", SUCCESS_COLOR);
        statsPanel.add(todayAttendanceCard);
        
        // Total sessions card
        JPanel sessionsCard = createStatCard("Total Sessions", "0", DANGER_COLOR);
        statsPanel.add(sessionsCard);
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(PANEL_COLOR);
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JTextArea welcomeText = new JTextArea();
        welcomeText.setText("Welcome to Student Attendance Management System!\n\n" +
                "This system helps you:\n" +
                "• Manage student records efficiently\n" +
                "• Mark daily attendance (Present/Absent)\n" +
                "• Calculate attendance percentages\n" +
                "• Generate detailed reports\n\n" +
                "Built using Java data structures:\n" +
                "• HashMap for student storage\n" +
                "• ArrayList for attendance logs\n" +
                "• Queue for session management\n\n" +
                "Navigate through the tabs above to access different features.");
        welcomeText.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeText.setEditable(false);
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setBackground(PANEL_COLOR);
        
        welcomePanel.add(welcomeText, BorderLayout.CENTER);
        panel.add(welcomePanel, BorderLayout.CENTER);
        
        // Refresh button
        JButton refreshBtn = createStyledButton("Refresh Dashboard", PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> refreshDashboard(totalStudentsCard, todayAttendanceCard, sessionsCard));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(250, 120));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setName(title); // For easy reference when updating
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void refreshDashboard(JPanel totalStudentsCard, JPanel todayAttendanceCard, JPanel sessionsCard) {
        // Update total students
        Component[] components1 = totalStudentsCard.getComponents();
        for (Component comp : components1) {
            if (comp instanceof JLabel && ((JLabel) comp).getName() != null) {
                ((JLabel) comp).setText(String.valueOf(attendanceManager.getTotalStudents()));
            }
        }
        
        // Update today's attendance
        ArrayList<AttendanceRecord> todayRecords = attendanceManager.getAttendanceByDate(LocalDate.now());
        int presentToday = 0;
        for (AttendanceRecord record : todayRecords) {
            if (record.isPresent()) presentToday++;
        }
        int totalToday = todayRecords.size();
        String attendancePercent = totalToday > 0 ? 
            String.format("%.1f%%", (presentToday * 100.0 / totalToday)) : "0%";
        
        Component[] components2 = todayAttendanceCard.getComponents();
        for (Component comp : components2) {
            if (comp instanceof JLabel && ((JLabel) comp).getName() != null) {
                ((JLabel) comp).setText(attendancePercent);
            }
        }
        
        // Update total sessions
        Component[] components3 = sessionsCard.getComponents();
        for (Component comp : components3) {
            if (comp instanceof JLabel && ((JLabel) comp).getName() != null) {
                ((JLabel) comp).setText(String.valueOf(attendanceManager.getAttendanceSessions().size()));
            }
        }
        
        JOptionPane.showMessageDialog(this, "Dashboard refreshed successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel for adding students
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Add New Student",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField studentIdField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField courseField = new JTextField(20);
        
        // Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(studentIdField, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        // Course
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1;
        formPanel.add(courseField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton addBtn = createStyledButton("Add Student", SUCCESS_COLOR);
        JButton clearBtn = createStyledButton("Clear", DANGER_COLOR);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table for displaying students
        String[] columns = {"Student ID", "Name", "Email", "Course"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.setRowHeight(30);
        studentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        studentTable.getTableHeader().setBackground(PRIMARY_COLOR);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Student List",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        JButton refreshBtn = createStyledButton("Refresh List", PRIMARY_COLOR);
        JButton deleteBtn = createStyledButton("Delete Selected", DANGER_COLOR);
        
        bottomPanel.add(refreshBtn);
        bottomPanel.add(deleteBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Event handlers
        addBtn.addActionListener(e -> {
            String id = studentIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String course = courseField.getText().trim();
            
            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Student student = new Student(id, name, email, course);
            if (attendanceManager.addStudent(student)) {
                tableModel.addRow(new Object[]{id, name, email, course});
                studentIdField.setText("");
                nameField.setText("");
                emailField.setText("");
                courseField.setText("");
                JOptionPane.showMessageDialog(this, "Student added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Student ID already exists!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        clearBtn.addActionListener(e -> {
            studentIdField.setText("");
            nameField.setText("");
            emailField.setText("");
            courseField.setText("");
        });
        
        refreshBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            for (Student student : attendanceManager.getAllStudents()) {
                tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getEmail(),
                    student.getCourse()
                });
            }
        });
        
        deleteBtn.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a student to delete!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String studentId = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this student?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (attendanceManager.removeStudent(studentId)) {
                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return panel;
    }
    
    private JPanel createAttendanceMarkingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel for date selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Select Date",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Date selector (using current date by default)
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(150, 30));
        
        JButton loadBtn = createStyledButton("Load Students", PRIMARY_COLOR);
        
        topPanel.add(dateLabel);
        topPanel.add(dateSpinner);
        topPanel.add(loadBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table for marking attendance
        String[] columns = {"Student ID", "Name", "Course", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only status column is editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) {
                    return Boolean.class;
                }
                return String.class;
            }
        };
        
        JTable attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 14));
        attendanceTable.setRowHeight(35);
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        attendanceTable.getTableHeader().setBackground(PRIMARY_COLOR);
        attendanceTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Mark Attendance",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        
        JButton markAllPresentBtn = createStyledButton("Mark All Present", SUCCESS_COLOR);
        JButton markAllAbsentBtn = createStyledButton("Mark All Absent", DANGER_COLOR);
        JButton saveBtn = createStyledButton("Save Attendance", PRIMARY_COLOR);
        
        bottomPanel.add(markAllPresentBtn);
        bottomPanel.add(markAllAbsentBtn);
        bottomPanel.add(saveBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Event handlers
        loadBtn.addActionListener(e -> {
            tableModel.setRowCount(0);
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
            
            for (Student student : attendanceManager.getAllStudents()) {
                // Check if attendance already exists for this date
                ArrayList<AttendanceRecord> records = attendanceManager.getAttendanceByDate(date);
                boolean present = false;
                for (AttendanceRecord record : records) {
                    if (record.getStudentId().equals(student.getStudentId())) {
                        present = record.isPresent();
                        break;
                    }
                }
                
                tableModel.addRow(new Object[]{
                    student.getStudentId(),
                    student.getName(),
                    student.getCourse(),
                    present
                });
            }
        });
        
        markAllPresentBtn.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(true, i, 3);
            }
        });
        
        markAllAbsentBtn.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(false, i, 3);
            }
        });
        
        saveBtn.addActionListener(e -> {
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Please load students first!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Date selectedDate = (Date) dateSpinner.getValue();
            LocalDate date = new java.sql.Date(selectedDate.getTime()).toLocalDate();
            
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String studentId = (String) tableModel.getValueAt(i, 0);
                Boolean present = (Boolean) tableModel.getValueAt(i, 3);
                attendanceManager.markAttendance(studentId, date, present != null ? present : false);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Attendance saved successfully for " + date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + "!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Top panel for student selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Generate Report",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        JLabel studentLabel = new JLabel("Student ID:");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField studentIdField = new JTextField(15);
        JButton generateBtn = createStyledButton("Generate Report", PRIMARY_COLOR);
        JButton viewAllBtn = createStyledButton("View All Reports", SUCCESS_COLOR);
        
        topPanel.add(studentLabel);
        topPanel.add(studentIdField);
        topPanel.add(generateBtn);
        topPanel.add(viewAllBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel for report display
        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 14));
        reportArea.setEditable(false);
        reportArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Report Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Event handlers
        generateBtn.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!attendanceManager.studentExists(studentId)) {
                JOptionPane.showMessageDialog(this, "Student not found!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Student student = attendanceManager.getStudent(studentId);
            Map<String, Integer> stats = attendanceManager.getStudentStatistics(studentId);
            double percentage = attendanceManager.calculateAttendancePercentage(studentId);
            ArrayList<AttendanceRecord> records = attendanceManager.getStudentAttendance(studentId);
            
            StringBuilder report = new StringBuilder();
            report.append("=".repeat(70)).append("\n");
            report.append("              STUDENT ATTENDANCE REPORT\n");
            report.append("=".repeat(70)).append("\n\n");
            
            report.append("STUDENT DETAILS:\n");
            report.append("-".repeat(70)).append("\n");
            report.append(String.format("Student ID    : %s\n", student.getStudentId()));
            report.append(String.format("Name          : %s\n", student.getName()));
            report.append(String.format("Email         : %s\n", student.getEmail()));
            report.append(String.format("Course        : %s\n\n", student.getCourse()));
            
            report.append("ATTENDANCE SUMMARY:\n");
            report.append("-".repeat(70)).append("\n");
            report.append(String.format("Total Days    : %d\n", stats.get("total")));
            report.append(String.format("Present       : %d\n", stats.get("present")));
            report.append(String.format("Absent        : %d\n", stats.get("absent")));
            report.append(String.format("Percentage    : %.2f%%\n\n", percentage));
            
            if (!records.isEmpty()) {
                report.append("ATTENDANCE HISTORY:\n");
                report.append("-".repeat(70)).append("\n");
                report.append(String.format("%-15s %-20s %-15s\n", "Date", "Status", "Remarks"));
                report.append("-".repeat(70)).append("\n");
                
                // Sort records by date
                records.sort(Comparator.comparing(AttendanceRecord::getDate));
                
                for (AttendanceRecord record : records) {
                    String status = record.isPresent() ? "PRESENT" : "ABSENT";
                    String remarks = record.isPresent() ? "✓" : "✗";
                    report.append(String.format("%-15s %-20s %-15s\n",
                        record.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        status,
                        remarks
                    ));
                }
            }
            
            report.append("\n").append("=".repeat(70)).append("\n");
            report.append("Report generated on: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n");
            report.append("=".repeat(70)).append("\n");
            
            reportArea.setText(report.toString());
        });
        
        viewAllBtn.addActionListener(e -> {
            StringBuilder report = new StringBuilder();
            report.append("=".repeat(70)).append("\n");
            report.append("           ALL STUDENTS ATTENDANCE SUMMARY\n");
            report.append("=".repeat(70)).append("\n\n");
            
            report.append(String.format("%-12s %-20s %-10s %-10s %-12s\n", 
                "Student ID", "Name", "Present", "Absent", "Percentage"));
            report.append("-".repeat(70)).append("\n");
            
            for (Student student : attendanceManager.getAllStudents()) {
                Map<String, Integer> stats = attendanceManager.getStudentStatistics(student.getStudentId());
                double percentage = attendanceManager.calculateAttendancePercentage(student.getStudentId());
                
                report.append(String.format("%-12s %-20s %-10d %-10d %-12.2f%%\n",
                    student.getStudentId(),
                    student.getName(),
                    stats.get("present"),
                    stats.get("absent"),
                    percentage
                ));
            }
            
            report.append("\n").append("=".repeat(70)).append("\n");
            report.append("Total Students: ").append(attendanceManager.getTotalStudents()).append("\n");
            report.append("Report generated on: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))).append("\n");
            report.append("=".repeat(70)).append("\n");
            
            reportArea.setText(report.toString());
        });
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void addSampleData() {
        // Add diverse sample students for demonstration
        attendanceManager.addStudent(new Student("S001", "John Doe", "john.doe@university.edu", "Computer Science"));
        attendanceManager.addStudent(new Student("S002", "Jane Smith", "jane.smith@university.edu", "Information Technology"));
        attendanceManager.addStudent(new Student("S003", "Bob Johnson", "bob.johnson@university.edu", "Electronics Engineering"));
        attendanceManager.addStudent(new Student("S004", "Alice Williams", "alice.williams@university.edu", "Computer Science"));
        attendanceManager.addStudent(new Student("S005", "Charlie Brown", "charlie.brown@university.edu", "Mechanical Engineering"));
        attendanceManager.addStudent(new Student("S006", "Diana Prince", "diana.prince@university.edu", "Information Technology"));
        attendanceManager.addStudent(new Student("S007", "Ethan Hunt", "ethan.hunt@university.edu", "Civil Engineering"));
        attendanceManager.addStudent(new Student("S008", "Fiona Green", "fiona.green@university.edu", "Computer Science"));
        attendanceManager.addStudent(new Student("S009", "George Miller", "george.miller@university.edu", "Electronics Engineering"));
        attendanceManager.addStudent(new Student("S010", "Hannah Lee", "hannah.lee@university.edu", "Information Technology"));
        
        // Add comprehensive attendance records over multiple days
        // This demonstrates Queue usage for session management and statistical features
        LocalDate today = LocalDate.now();
        
        // Week 1 - 5 days of attendance (demonstrates regular attendance patterns)
        for (int i = 0; i < 5; i++) {
            LocalDate date = today.minusDays(i);
            // Students with excellent attendance (95%+)
            attendanceManager.markAttendance("S001", date, true);
            attendanceManager.markAttendance("S004", date, i != 0); // 80%
            
            // Students with good attendance (80-90%)
            attendanceManager.markAttendance("S002", date, i < 4); // 80%
            attendanceManager.markAttendance("S006", date, i < 4); // 80%
            
            // Students with average attendance (70-80%)
            attendanceManager.markAttendance("S003", date, i < 3 || i == 4); // 80%
            attendanceManager.markAttendance("S008", date, i != 1 && i != 2); // 60%
            
            // Students with poor attendance (below 70%)
            attendanceManager.markAttendance("S005", date, i < 3); // 60%
            attendanceManager.markAttendance("S007", date, i < 2); // 40%
            
            // Students with mixed patterns
            attendanceManager.markAttendance("S009", date, i % 2 == 0); // 60%
            attendanceManager.markAttendance("S010", date, i != 0 && i != 3); // 60%
        }
        
        // Week 2 - Additional 5 days (demonstrates longer attendance history)
        for (int i = 5; i < 10; i++) {
            LocalDate date = today.minusDays(i);
            // Continue patterns for statistical diversity
            attendanceManager.markAttendance("S001", date, true); // Perfect attendance
            attendanceManager.markAttendance("S002", date, i < 9); // Good
            attendanceManager.markAttendance("S003", date, i % 2 == 0); // Average
            attendanceManager.markAttendance("S004", date, i < 8); // Good
            attendanceManager.markAttendance("S005", date, i < 7); // Poor
            attendanceManager.markAttendance("S006", date, i != 6); // Good
            attendanceManager.markAttendance("S007", date, false); // Very poor
            attendanceManager.markAttendance("S008", date, i % 3 == 0); // Poor
            attendanceManager.markAttendance("S009", date, i % 2 == 1); // Average
            attendanceManager.markAttendance("S010", date, i < 8); // Good
        }
        
        // Week 3 - Recent 5 days (demonstrates recent trends)
        for (int i = 10; i < 15; i++) {
            LocalDate date = today.minusDays(i);
            attendanceManager.markAttendance("S001", date, true); // Consistent excellent
            attendanceManager.markAttendance("S002", date, i < 14); // Improving
            attendanceManager.markAttendance("S003", date, i < 13); // Improving
            attendanceManager.markAttendance("S004", date, true); // Consistent excellent
            attendanceManager.markAttendance("S005", date, i >= 12); // Declining
            attendanceManager.markAttendance("S006", date, i != 11); // Good
            attendanceManager.markAttendance("S007", date, i == 14); // Very poor
            attendanceManager.markAttendance("S008", date, i >= 13); // Declining
            attendanceManager.markAttendance("S009", date, i % 2 == 0); // Consistent average
            attendanceManager.markAttendance("S010", date, i < 13); // Good
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttendanceSystemUI ui = new AttendanceSystemUI();
            ui.setVisible(true);
        });
    }
}
