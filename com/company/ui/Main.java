package com.company.ui;

import com.company.db.DatabaseManager;
import com.company.utils.PasswordUtil;
import static com.company.ui.UIConstants.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel loginPanel, studentPanel, attendancePanel, reportPanel, teacherPanel, subjectPanel, userPanel, dashboardPanel;

    // Login components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton loginBtn;

    // Current user info
    private String currentUser;
    private String currentRole;
    private int currentUserId;
    private JLabel lblLoggedInAs;

    // Student Management Components
    private JTextField firstNameField, lastNameField, rollField, studentSearchField, studentEmailField, studentPhoneField;
    private JComboBox<String> studentClassComboBox, studentDivisionComboBox;
    private JButton addStudentBtn, updateStudentBtn, deleteStudentBtn, searchStudentBtn, refreshStudentBtn;
    private JTable studentTable;
    private DefaultTableModel studentTableModel;

    // Attendance Management Components
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> attendanceClassComboBox, attendanceDivisionComboBox;
    private JComboBox<String> sessionNumberComboBox;
    private JSpinner dateSpinner;
    private JTable attendanceTable;
    private JButton markAttendanceBtn;
    private DefaultTableModel attendanceTableModel;

    // Attendance Reporting Components (removed - now using ReportGenerator)

    // Teacher Management Components
    private JTextField teacherFirstNameField, teacherLastNameField, teacherEmailField, teacherPhoneField;
    private JButton addTeacherBtn, updateTeacherBtn, deleteTeacherBtn;
    private JTable teacherTable;
    private DefaultTableModel teacherTableModel;

    // Subject Management Components
    private JTextField subjectNameField, subjectCodeField;
    private JButton addSubjectBtn, deleteSubjectBtn;
    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;

    // User Management Components
    private JComboBox<String> userRoleComboBox;
    private JTextField newUsernameField;
    private JPasswordField newPasswordField;
    private JButton createUserBtn, deleteUserBtn;
    private JTable userTable;
    private DefaultTableModel userTableModel;

    // Dashboard Components (removed - now using DashboardEnhanced)

    public Main() {
        setTitle("Student Attendance Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create login panel first
        loginPanel = createLoginPanel();
        add(loginPanel);

        setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create title panel
        JPanel titlePanel = createTitlePanel("Student Attendance System", "Manage student attendance efficiently");
        
        // Create center panel with card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel cardPanel = createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(400, 350));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        
        JLabel loginTitle = createStyledLabel("Sign In", SUBTITLE_FONT);
        loginTitle.setHorizontalAlignment(JLabel.CENTER);
        cardPanel.add(loginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel usernameLabel = createStyledLabel("Username:", NORMAL_FONT);
        cardPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = createStyledTextField(15);
        cardPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = createStyledLabel("Password:", NORMAL_FONT);
        cardPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = createStyledPasswordField(15);
        cardPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel roleLabel = createStyledLabel("Role:", NORMAL_FONT);
        cardPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        roleComboBox = createStyledComboBox(new String[]{"Admin", "Teacher", "Student"});
        cardPanel.add(roleComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        loginBtn = createPrimaryButton("Login");
        loginBtn.setPreferredSize(new Dimension(350, BUTTON_HEIGHT));
        cardPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        
        centerPanel.add(cardPanel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", 
                "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, role);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                // Verify password with plain text comparison
                boolean passwordMatch = PasswordUtil.verifyPassword(password, storedPassword);
                
                if (passwordMatch) {
                    currentUser = username;
                    currentRole = rs.getString("role");
                    currentUserId = rs.getInt("user_id");

                    // Remove login panel and show main application
                    remove(loginPanel);
                    initializeMainApplication();
                    lblLoggedInAs.setText("Logged in as: " + currentUser + " (" + currentRole + ")");

                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + username,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username, password, or role.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username, password, or role.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            showError("Login", ex);
        }
    }

    private void initializeMainApplication() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);

        // Create panels based on user role
        if ("Admin".equals(currentRole)) {
            dashboardPanel = createDashboardPanel();
            studentPanel = createStudentPanel();
            teacherPanel = createTeacherPanel();
            subjectPanel = createSubjectPanel();
            userPanel = createUserPanel();
            attendancePanel = createAttendancePanel();
            reportPanel = createReportPanel();

            tabbedPane.addTab("Dashboard", dashboardPanel);
            tabbedPane.addTab("Students", studentPanel);
            tabbedPane.addTab("Teachers", teacherPanel);
            tabbedPane.addTab("Subjects", subjectPanel);
            tabbedPane.addTab("Users", userPanel);
            tabbedPane.addTab("Attendance", attendancePanel);
            tabbedPane.addTab("Reports", reportPanel);
        } else if ("Teacher".equals(currentRole)) {
            // Add teacher-specific dashboard
            JPanel teacherDashboard = DashboardEnhanced.createTeacherDashboard(getTeacherIdForUser(currentUserId));
            attendancePanel = createAttendancePanel();
            reportPanel = createReportPanel();
            loadSubjects();

            tabbedPane.addTab("Dashboard", teacherDashboard);
            tabbedPane.addTab("Attendance", attendancePanel);
            tabbedPane.addTab("Reports", reportPanel);
        } else if ("Student".equals(currentRole)) {
            // Add student-specific dashboard
            JPanel studentDashboard = DashboardEnhanced.createStudentDashboard(currentUserId);
            reportPanel = createReportPanel();
            loadSubjects();

            tabbedPane.addTab("Dashboard", studentDashboard);
            tabbedPane.addTab("Reports", reportPanel);
        }

        // Add logout button
        JButton logoutBtn = createDangerButton("Logout");
        logoutBtn.addActionListener(e -> logout());

        // Use the class-level label
        lblLoggedInAs = createStyledLabel("", NORMAL_FONT);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(lblLoggedInAs);
        southPanel.add(logoutBtn);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Initial load based on role
        if ("Admin".equals(currentRole)) {
            loadStudents();
            loadTeachers();
            loadSubjects();
            loadUsers();
        }

        // Listeners for populating tables when a tab is selected
        tabbedPane.addChangeListener(e -> {
            if ("Admin".equals(currentRole)) {
                if (tabbedPane.getSelectedComponent() == studentPanel) {
                    loadStudents();
                } else if (tabbedPane.getSelectedComponent() == teacherPanel) {
                    loadTeachers();
                } else if (tabbedPane.getSelectedComponent() == subjectPanel) {
                    loadSubjects();
                } else if (tabbedPane.getSelectedComponent() == userPanel) {
                    loadUsers();
                }
            }
            if (tabbedPane.getSelectedComponent() == attendancePanel) {
                populateAttendanceTable();
            }
        });

        // Row select -> fill form
        if (studentTable != null) {
            studentTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int r = studentTable.getSelectedRow();
                    if (r != -1) {
                        firstNameField.setText(studentTableModel.getValueAt(r, 1).toString());
                        lastNameField.setText(studentTableModel.getValueAt(r, 2).toString());
                        rollField.setText(studentTableModel.getValueAt(r, 3).toString());
                        if (studentTableModel.getColumnCount() > 4 && studentTableModel.getValueAt(r, 4) != null) {
                            studentEmailField.setText(studentTableModel.getValueAt(r, 4).toString());
                        }
                        if (studentTableModel.getColumnCount() > 5 && studentTableModel.getValueAt(r, 5) != null) {
                            studentPhoneField.setText(studentTableModel.getValueAt(r, 5).toString());
                        }
                        if (studentTableModel.getColumnCount() > 6 && studentTableModel.getValueAt(r, 6) != null) {
                            String classValue = studentTableModel.getValueAt(r, 6).toString();
                            studentClassComboBox.setSelectedItem(classValue);
                        }
                        if (studentTableModel.getColumnCount() > 7 && studentTableModel.getValueAt(r, 7) != null) {
                            String divValue = studentTableModel.getValueAt(r, 7).toString();
                            studentDivisionComboBox.setSelectedItem(divValue);
                        }
                    }
                }
            });
        }

        if (teacherTable != null) {
            teacherTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int r = teacherTable.getSelectedRow();
                    if (r != -1) {
                        teacherFirstNameField.setText(teacherTableModel.getValueAt(r, 1).toString());
                        teacherLastNameField.setText(teacherTableModel.getValueAt(r, 2).toString());
                        teacherEmailField.setText(teacherTableModel.getValueAt(r, 3).toString());
                        if (teacherTableModel.getColumnCount() > 4 && teacherTableModel.getValueAt(r, 4) != null) {
                            teacherPhoneField.setText(teacherTableModel.getValueAt(r, 4).toString());
                        }
                    }
                }
            });
        }

        if (subjectTable != null) {
            subjectTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int r = subjectTable.getSelectedRow();
                    if (r != -1) {
                        subjectNameField.setText(subjectTableModel.getValueAt(r, 1).toString());
                        if (subjectTableModel.getColumnCount() > 2 && subjectTableModel.getValueAt(r, 2) != null) {
                            subjectCodeField.setText(subjectTableModel.getValueAt(r, 2).toString());
                        }
                    }
                }
            });
        }
        if (userTable != null) {
            userTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int r = userTable.getSelectedRow();
                    if (r != -1) {
                        newUsernameField.setText(userTableModel.getValueAt(r, 1).toString());
                        userRoleComboBox.setSelectedItem(userTableModel.getValueAt(r, 2).toString());
                    }
                }
            });
        }

        revalidate();
        repaint();
    }

    private void logout() {
        currentUser = null;
        currentRole = null;
        currentUserId = -1;
        lblLoggedInAs.setText("");
        remove(tabbedPane);
        add(loginPanel);
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);

        revalidate();
        repaint();
    }

    // ----------------- Panels -----------------
    private JPanel createDashboardPanel() {
        // Use enhanced dashboard with charts and better visualization
        return DashboardEnhanced.createAdminDashboard();
    }
    


    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Student Management", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Form panel with improved layout
        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        firstNameField = createStyledTextField(15);
        lastNameField = createStyledTextField(15);
        rollField = createStyledTextField(15);
        studentEmailField = createStyledTextField(15);
        studentPhoneField = createStyledTextField(15);
        studentClassComboBox = createStyledComboBox(new String[]{"", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8"});
        studentDivisionComboBox = createStyledComboBox(new String[]{"", "A", "B", "C", "D", "E"});

        addStudentBtn = createSuccessButton("➕ Add");
        updateStudentBtn = createPrimaryButton("✏️ Update");
        deleteStudentBtn = createDangerButton("🗑️ Delete");
        
        // Layout form in 2 columns
        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createStyledLabel("First Name:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(firstNameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formCard.add(createStyledLabel("Last Name:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(lastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createStyledLabel("Roll No:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(rollField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formCard.add(createStyledLabel("Email:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(studentEmailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formCard.add(createStyledLabel("Phone:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(studentPhoneField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        formCard.add(createStyledLabel("Class:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(studentClassComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formCard.add(createStyledLabel("Division:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(studentDivisionComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3;
        formCard.add(addStudentBtn, gbc);
        gbc.gridx = 3;
        formCard.add(updateStudentBtn, gbc);

        // Search panel with better styling
        JPanel searchCard = createCardPanel();
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchCard.setPreferredSize(new Dimension(0, 60));
        
        studentSearchField = createStyledTextField(20);
        searchStudentBtn = createPrimaryButton("🔍 Search");
        refreshStudentBtn = createPrimaryButton("🔄 Refresh");
        
        searchCard.add(createStyledLabel("Search:", NORMAL_FONT));
        searchCard.add(studentSearchField);
        searchCard.add(searchStudentBtn);
        searchCard.add(refreshStudentBtn);
        searchCard.add(deleteStudentBtn);

        studentTableModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name", "Roll No.", "Email", "Phone", "Class", "Division"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        studentTable = new JTable(studentTableModel);
        styleTable(studentTable);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(60);

        addStudentBtn.addActionListener(e -> addStudent());
        updateStudentBtn.addActionListener(e -> updateStudent());
        deleteStudentBtn.addActionListener(e -> deleteStudent());
        searchStudentBtn.addActionListener(e -> searchStudent());
        refreshStudentBtn.addActionListener(e -> loadStudents());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(formCard, BorderLayout.CENTER);
        topPanel.add(searchCard, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Teacher Management", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        teacherFirstNameField = createStyledTextField(15);
        teacherLastNameField = createStyledTextField(15);
        teacherEmailField = createStyledTextField(15);
        teacherPhoneField = createStyledTextField(15);

        addTeacherBtn = createSuccessButton("➕ Add Teacher");
        updateTeacherBtn = createPrimaryButton("✏️ Update");
        deleteTeacherBtn = createDangerButton("🗑️ Delete");

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createStyledLabel("First Name:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(teacherFirstNameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formCard.add(createStyledLabel("Last Name:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(teacherLastNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createStyledLabel("Email:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(teacherEmailField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formCard.add(createStyledLabel("Phone:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(teacherPhoneField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        formCard.add(addTeacherBtn, gbc);
        gbc.gridx = 2;
        formCard.add(updateTeacherBtn, gbc);
        gbc.gridx = 3;
        formCard.add(deleteTeacherBtn, gbc);

        teacherTableModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name", "Email", "Phone"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        teacherTable = new JTable(teacherTableModel);
        styleTable(teacherTable);
        teacherTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        teacherTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        teacherTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        teacherTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        teacherTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        addTeacherBtn.addActionListener(e -> addTeacher());
        updateTeacherBtn.addActionListener(e -> updateTeacher());
        deleteTeacherBtn.addActionListener(e -> deleteTeacher());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(formCard, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(teacherTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Subject Management", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        subjectNameField = createStyledTextField(20);
        subjectCodeField = createStyledTextField(15);
        addSubjectBtn = createSuccessButton("➕ Add Subject");
        deleteSubjectBtn = createDangerButton("🗑️ Delete");

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createStyledLabel("Subject Name:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(subjectNameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formCard.add(createStyledLabel("Subject Code:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(subjectCodeField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        formCard.add(addSubjectBtn, gbc);
        gbc.gridx = 2;
        formCard.add(deleteSubjectBtn, gbc);

        subjectTableModel = new DefaultTableModel(
            new String[]{"ID", "Subject Name", "Subject Code"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        subjectTable = new JTable(subjectTableModel);
        styleTable(subjectTable);
        subjectTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        subjectTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        subjectTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        addSubjectBtn.addActionListener(e -> addSubject());
        deleteSubjectBtn.addActionListener(e -> deleteSubject());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(formCard, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("User Management", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        JPanel formCard = createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        newUsernameField = createStyledTextField(15);
        newPasswordField = createStyledPasswordField(15);
        userRoleComboBox = createStyledComboBox(new String[]{"Admin", "Teacher", "Student"});
        createUserBtn = createSuccessButton("➕ Create User");
        deleteUserBtn = createDangerButton("🗑️ Delete");

        gbc.gridx = 0; gbc.gridy = 0;
        formCard.add(createStyledLabel("Username:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(newUsernameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formCard.add(createStyledLabel("Password:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        formCard.add(newPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formCard.add(createStyledLabel("Role:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        formCard.add(userRoleComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formCard.add(createUserBtn, gbc);
        gbc.gridx = 3;
        formCard.add(deleteUserBtn, gbc);

        userTableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        styleTable(userTable);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(150);

        createUserBtn.addActionListener(e -> createUser());
        deleteUserBtn.addActionListener(e -> deleteUser());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(formCard, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Attendance Marking", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        JPanel topCard = createCardPanel();
        topCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        subjectComboBox = createStyledComboBox(new String[]{});
        attendanceClassComboBox = createStyledComboBox(new String[]{"All", "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8"});
        attendanceDivisionComboBox = createStyledComboBox(new String[]{"All", "A", "B", "C", "D", "E"});
        sessionNumberComboBox = createStyledComboBox(new String[]{"Session 1", "Session 2", "Session 3", "Session 4", "Session 5", "Session 6", "Session 7", "Session 8", "Session 9", "Session 10"});
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(NORMAL_FONT);
        markAttendanceBtn = createSuccessButton("💾 Save Attendance");
        
        JButton loadClassBtn = createPrimaryButton("🔄 Load Students");

        topCard.add(createStyledLabel("Subject:", NORMAL_FONT));
        topCard.add(subjectComboBox);
        topCard.add(createStyledLabel("Session:", NORMAL_FONT));
        topCard.add(sessionNumberComboBox);
        topCard.add(createStyledLabel("Class:", NORMAL_FONT));
        topCard.add(attendanceClassComboBox);
        topCard.add(createStyledLabel("Division:", NORMAL_FONT));
        topCard.add(attendanceDivisionComboBox);
        topCard.add(createStyledLabel("Date:", NORMAL_FONT));
        topCard.add(dateSpinner);
        topCard.add(loadClassBtn);
        topCard.add(markAttendanceBtn);
        
        loadClassBtn.addActionListener(e -> populateAttendanceTable());

        attendanceTable = new JTable(new DefaultTableModel(
            new String[]{"Student ID", "First Name", "Last Name", "Roll No.", "Class", "Division", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return ("Teacher".equals(currentRole) || "Admin".equals(currentRole)) && c == 6;
            }
        });
        styleTable(attendanceTable);
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        attendanceTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        attendanceTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        attendanceTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        if ("Teacher".equals(currentRole) || "Admin".equals(currentRole)) {
            JComboBox<String> statusCombo = createStyledComboBox(
                new String[]{"Present", "Absent", "Late", "Excused"});
            attendanceTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(statusCombo));
        }

        markAttendanceBtn.addActionListener(e -> markAttendance());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(topCard, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportPanel() {
        // Use enhanced report generator with better formatting and export options
        return com.company.ui.ReportGenerator.createReportPanel(currentRole, currentUserId);
    }

    // --------------- Student Logic ---------------
    private void loadStudents() {
        studentTableModel.setRowCount(0);
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                studentTableModel.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("student_roll"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("class"),
                        rs.getString("division")
                });
            }
        } catch (SQLException ex) {
            showError("Loading students", ex);
        }
    }

    private void addStudent() {
        String f = firstNameField.getText().trim();
        String l = lastNameField.getText().trim();
        String r = rollField.getText().trim();
        String email = studentEmailField.getText().trim();
        String phone = studentPhoneField.getText().trim();
        String studentClass = (String) studentClassComboBox.getSelectedItem();
        String division = (String) studentDivisionComboBox.getSelectedItem();
        
        if (f.isEmpty() || l.isEmpty() || r.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Roll No are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email if provided
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO students(first_name, last_name, student_roll, email, phone, class, division) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, l);
            ps.setString(3, r);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.setString(5, phone.isEmpty() ? null : phone);
            ps.setString(6, studentClass != null && !studentClass.isEmpty() ? studentClass : null);
            ps.setString(7, division != null && !division.isEmpty() ? division : null);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            firstNameField.setText("");
            lastNameField.setText("");
            rollField.setText("");
            studentEmailField.setText("");
            studentPhoneField.setText("");
            studentClassComboBox.setSelectedIndex(0);
            studentDivisionComboBox.setSelectedIndex(0);
            loadStudents();
            populateAttendanceTable();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Roll number already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Adding student", ex);
            }
        }
    }

    private void updateStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to update.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) studentTableModel.getValueAt(row, 0);
        String f = firstNameField.getText().trim();
        String l = lastNameField.getText().trim();
        String r = rollField.getText().trim();
        String email = studentEmailField.getText().trim();
        String phone = studentPhoneField.getText().trim();
        String studentClass = (String) studentClassComboBox.getSelectedItem();
        String division = (String) studentDivisionComboBox.getSelectedItem();
        
        if (f.isEmpty() || l.isEmpty() || r.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Roll No are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email if provided
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "UPDATE students SET first_name=?, last_name=?, student_roll=?, email=?, phone=?, class=?, division=? WHERE student_id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, l);
            ps.setString(3, r);
            ps.setString(4, email.isEmpty() ? null : email);
            ps.setString(5, phone.isEmpty() ? null : phone);
            ps.setString(6, studentClass != null && !studentClass.isEmpty() ? studentClass : null);
            ps.setString(7, division != null && !division.isEmpty() ? division : null);
            ps.setInt(8, id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
                populateAttendanceTable();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Roll number already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Updating student", ex);
            }
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) studentTableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudents();
            populateAttendanceTable();
        } catch (SQLException ex) {
            showError("Deleting student", ex);
        }
    }

    private void searchStudent() {
        String q = studentSearchField.getText().trim();
        if (q.isEmpty()) {
            loadStudents();
            return;
        }
        studentTableModel.setRowCount(0);
        String sql = "SELECT * FROM students WHERE first_name ILIKE ? OR last_name ILIKE ? OR student_roll = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            ps.setString(2, "%" + q + "%");
            ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    studentTableModel.addRow(new Object[]{
                            rs.getInt("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("student_roll"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("class"),
                            rs.getString("division")
                    });
                }
            }
        } catch (SQLException ex) {
            showError("Searching", ex);
        }
    }

    // --------------- Teacher Logic ---------------
    private void loadTeachers() {
        teacherTableModel.setRowCount(0);
        String sql = "SELECT * FROM teachers ORDER BY teacher_id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                teacherTableModel.addRow(new Object[]{
                        rs.getInt("teacher_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone")
                });
            }
        } catch (SQLException ex) {
            showError("Loading teachers", ex);
        }
    }

    private void addTeacher() {
        String f = teacherFirstNameField.getText().trim();
        String l = teacherLastNameField.getText().trim();
        String e = teacherEmailField.getText().trim();
        String phone = teacherPhoneField.getText().trim();
        
        if (f.isEmpty() || l.isEmpty() || e.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email
        if (!e.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO teachers(first_name, last_name, email, phone) VALUES(?, ?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, l);
            ps.setString(3, e);
            ps.setString(4, phone.isEmpty() ? null : phone);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Teacher added successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            teacherFirstNameField.setText("");
            teacherLastNameField.setText("");
            teacherEmailField.setText("");
            teacherPhoneField.setText("");
            loadTeachers();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Email already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Adding teacher", ex);
            }
        }
    }

    private void updateTeacher() {
        int row = teacherTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a teacher to update.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) teacherTableModel.getValueAt(row, 0);
        String f = teacherFirstNameField.getText().trim();
        String l = teacherLastNameField.getText().trim();
        String e = teacherEmailField.getText().trim();
        String phone = teacherPhoneField.getText().trim();
        
        if (f.isEmpty() || l.isEmpty() || e.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate email
        if (!e.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "UPDATE teachers SET first_name=?, last_name=?, email=?, phone=? WHERE teacher_id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, f);
            ps.setString(2, l);
            ps.setString(3, e);
            ps.setString(4, phone.isEmpty() ? null : phone);
            ps.setInt(5, id);
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Teacher updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeachers();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Email already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Updating teacher", ex);
            }
        }
    }

    private void deleteTeacher() {
        int row = teacherTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a teacher to delete.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) teacherTableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this teacher?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM teachers WHERE teacher_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Teacher deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadTeachers();
        } catch (SQLException ex) {
            showError("Deleting teacher", ex);
        }
    }

    // --------------- Subject Logic ---------------
    private void loadSubjects() {
        subjectTableModel.setRowCount(0);
        // Clear combo boxes to avoid duplicates
        subjectComboBox.removeAllItems();

        String sql = "SELECT * FROM subjects ORDER BY subject_id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String subjectName = rs.getString("subject_name");
                String subjectCode = rs.getString("subject_code");
                subjectTableModel.addRow(new Object[]{
                        rs.getInt("subject_id"),
                        subjectName,
                        subjectCode
                });
                subjectComboBox.addItem(subjectName);
            }
        } catch (SQLException ex) {
            showError("Loading subjects", ex);
        }
    }

    private void addSubject() {
        String name = subjectNameField.getText().trim();
        String code = subjectCodeField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject name is required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String sql = "INSERT INTO subjects(subject_name, subject_code) VALUES(?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, code.isEmpty() ? null : code);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject added successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            subjectNameField.setText("");
            subjectCodeField.setText("");
            loadSubjects();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Subject name already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Adding subject", ex);
            }
        }
    }

    private void deleteSubject() {
        int row = subjectTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) subjectTableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this subject?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM subjects WHERE subject_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSubjects();
        } catch (SQLException ex) {
            showError("Deleting subject", ex);
        }
    }

    // --------------- User Logic ---------------
    private void loadUsers() {
        userTableModel.setRowCount(0);
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userTableModel.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }
        } catch (SQLException ex) {
            showError("Loading users", ex);
        }
    }

    private void createUser() {
        String username = newUsernameField.getText().trim();
        String password = new String(newPasswordField.getPassword());
        String role = (String) userRoleComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Store the password as plain text
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User created successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            newUsernameField.setText("");
            newPasswordField.setText("");
            loadUsers();
        } catch (SQLException ex) {
            if (ex.getMessage().contains("unique") || ex.getMessage().contains("duplicate")) {
                JOptionPane.showMessageDialog(this, "Username already exists.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                showError("Creating user", ex);
            }
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) userTableModel.getValueAt(row, 0);
        String username = (String) userTableModel.getValueAt(row, 1);
        
        // Prevent deleting current user
        if (username.equals(currentUser)) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
        } catch (SQLException ex) {
            showError("Deleting user", ex);
        }
    }

    // --------------- Attendance ---------------
    private void populateAttendanceTable() {
        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Student ID", "First Name", "Last Name", "Roll No.", "Class", "Division", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) {
                return ("Teacher".equals(currentRole) || "Admin".equals(currentRole)) && c == 6;
            }
        };
        attendanceTable.setModel(m);
        styleTable(attendanceTable);

        if ("Teacher".equals(currentRole) || "Admin".equals(currentRole)) {
            JComboBox<String> statusCombo = createStyledComboBox(
                new String[]{"Present", "Absent", "Late", "Excused"});
            attendanceTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(statusCombo));
        }

        // Get selected class and division
        String selectedClass = (String) attendanceClassComboBox.getSelectedItem();
        String selectedDivision = (String) attendanceDivisionComboBox.getSelectedItem();
        
        // Build SQL query based on filters
        StringBuilder sql = new StringBuilder("SELECT student_id, first_name, last_name, student_roll, class, division FROM students WHERE 1=1");
        
        if (selectedClass != null && !selectedClass.equals("All")) {
            sql.append(" AND class = ?");
        }
        if (selectedDivision != null && !selectedDivision.equals("All")) {
            sql.append(" AND division = ?");
        }
        sql.append(" ORDER BY class, division, student_id");
        
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (selectedClass != null && !selectedClass.equals("All")) {
                ps.setString(paramIndex++, selectedClass);
            }
            if (selectedDivision != null && !selectedDivision.equals("All")) {
                ps.setString(paramIndex++, selectedDivision);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    m.addRow(new Object[]{
                            rs.getInt("student_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("student_roll"),
                            rs.getString("class"),
                            rs.getString("division"),
                            "Present"
                    });
                }
            }
        } catch (SQLException ex) {
            showError("Loading students for attendance", ex);
        }
    }

    private void markAttendance() {
        if (!("Teacher".equals(currentRole) || "Admin".equals(currentRole))) {
            JOptionPane.showMessageDialog(this, "You don't have permission to mark attendance.",
                "Permission Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String subjectName = (String) subjectComboBox.getSelectedItem();
        String sessionNumberStr = (String) sessionNumberComboBox.getSelectedItem();
        Date d = (Date) dateSpinner.getValue();
        if (subjectName == null) {
            JOptionPane.showMessageDialog(this, "Please add subjects first.",
                "No Subjects", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Extract session number from "Session X" format
        int sessionNumber = Integer.parseInt(sessionNumberStr.split(" ")[1]);

        String findSub = "SELECT subject_id FROM subjects WHERE subject_name=?";
        String findSession = "SELECT session_id FROM sessions WHERE session_date=? AND subject_id=? AND session_number=?";
        String insertSession = "INSERT INTO sessions(session_date, subject_id, session_number) VALUES(?,?,?)";
        // PostgreSQL upsert syntax (replace MySQL's ON DUPLICATE KEY UPDATE)
        String upsertAttendance = "INSERT INTO attendance(student_id, session_id, status) VALUES(?,?,?) " +
                                  "ON CONFLICT (student_id, session_id) DO UPDATE SET status = EXCLUDED.status";

        Connection c = null;
        try {
            c = DatabaseManager.getConnection();
            c.setAutoCommit(false);

            int subjectId;
            try (PreparedStatement ps = c.prepareStatement(findSub)) {
                ps.setString(1, subjectName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Subject not found.");
                    subjectId = rs.getInt(1);
                }
            }

            int sessionId;
            java.sql.Date sqlDate = new java.sql.Date(d.getTime());
            try (PreparedStatement ps = c.prepareStatement(findSession)) {
                ps.setDate(1, sqlDate);
                ps.setInt(2, subjectId);
                ps.setInt(3, sessionNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sessionId = rs.getInt(1);
                    } else {
                        try (PreparedStatement ins = c.prepareStatement(insertSession, Statement.RETURN_GENERATED_KEYS)) {
                            ins.setDate(1, sqlDate);
                            ins.setInt(2, subjectId);
                            ins.setInt(3, sessionNumber);
                            ins.executeUpdate();
                            try (ResultSet g = ins.getGeneratedKeys()) {
                                g.next();
                                sessionId = g.getInt(1);
                            }
                        }
                    }
                }
            }

            try (PreparedStatement ps = c.prepareStatement(upsertAttendance)) {
                DefaultTableModel m = (DefaultTableModel) attendanceTable.getModel();
                for (int i = 0; i < m.getRowCount(); i++) {
                    int studentId = (int) m.getValueAt(i, 0);
                    String status = (String) m.getValueAt(i, 6);
                    ps.setInt(1, studentId);
                    ps.setInt(2, sessionId);
                    ps.setString(3, status);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            c.commit();
            JOptionPane.showMessageDialog(this, 
                "Attendance saved successfully for " + new SimpleDateFormat("yyyy-MM-dd").format(d) + " - " + sessionNumberStr + " (" + subjectName + ")",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            try {
                if (c != null) c.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            showError("Marking attendance", ex);
        } finally {
            try {
                if (c != null) {
                    c.setAutoCommit(true);
                    c.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --------------- Reporting ---------------
    private int getTeacherIdForUser(int userId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT teacher_id FROM teachers WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getInt("teacher_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showError(String where, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, 
            "Error " + where + ":\n" + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}



