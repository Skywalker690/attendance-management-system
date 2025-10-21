package com.company.ui;

import com.company.db.DatabaseManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.company.ui.UIConstants.*;

/**
 * Enhanced report generator with multiple export formats
 */
public class ReportGenerator {

    /**
     * Create an enhanced report panel with filtering and export options
     */
    public static JPanel createReportPanel(String currentRole, int currentUserId) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Attendance Reports", SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Filter panel
        JPanel filterCard = createCardPanel();
        filterCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Filter components
        JTextField rollField = createStyledTextField(12);
        // Add placeholder hint for admins
        if ("Admin".equals(currentRole) || "Teacher".equals(currentRole)) {
            rollField.setToolTipText("Leave empty to show all students");
        }

        JComboBox<String> subjectCombo = createStyledComboBox(new String[]{});
        JComboBox<String> statusCombo = createStyledComboBox(
                new String[]{"All", "Present", "Absent", "Late", "Excused"});

        JSpinner fromDate = new JSpinner(new SpinnerDateModel(
                new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner toDate = new JSpinner(new SpinnerDateModel(
                new Date(), null, null, Calendar.DAY_OF_MONTH));
        fromDate.setEditor(new JSpinner.DateEditor(fromDate, "yyyy-MM-dd"));
        toDate.setEditor(new JSpinner.DateEditor(toDate, "yyyy-MM-dd"));
        fromDate.setFont(NORMAL_FONT);
        toDate.setFont(NORMAL_FONT);

        JButton generateBtn = createPrimaryButton("Generate Report");
        JButton exportCSVBtn = createSuccessButton("Export CSV");
        JButton exportTextBtn = createSuccessButton("Export Text");
        JButton printBtn = createPrimaryButton("Print");

        // Layout filters
        gbc.gridx = 0; gbc.gridy = 0;
        String rollLabelText = ("Admin".equals(currentRole) || "Teacher".equals(currentRole))
                ? "Student Roll (optional):" : "Student Roll:";
        filterCard.add(createStyledLabel(rollLabelText, NORMAL_FONT), gbc);
        gbc.gridx = 1;
        filterCard.add(rollField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        filterCard.add(createStyledLabel("Subject:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        filterCard.add(subjectCombo, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        filterCard.add(createStyledLabel("Status:", NORMAL_FONT), gbc);
        gbc.gridx = 5;
        filterCard.add(statusCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        filterCard.add(createStyledLabel("From Date:", NORMAL_FONT), gbc);
        gbc.gridx = 1;
        filterCard.add(fromDate, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        filterCard.add(createStyledLabel("To Date:", NORMAL_FONT), gbc);
        gbc.gridx = 3;
        filterCard.add(toDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        filterCard.add(generateBtn, gbc);
        gbc.gridx = 1;
        filterCard.add(exportCSVBtn, gbc);
        gbc.gridx = 2;
        filterCard.add(exportTextBtn, gbc);
        gbc.gridx = 3;
        filterCard.add(printBtn, gbc);

        // Report display area with table
        DefaultTableModel reportModel = new DefaultTableModel(
                new String[]{"Date", "Subject", "Session", "Student", "Roll No", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reportTable = new JTable(reportModel);
        styleTable(reportTable);
        JScrollPane reportScroll = new JScrollPane(reportTable);

        // Summary panel
        JPanel summaryPanel = createCardPanel();
        summaryPanel.setLayout(new GridLayout(1, 5, 10, 10));
        summaryPanel.setPreferredSize(new Dimension(0, 80));

        JLabel totalLabel = createStyledLabel("Total: 0", HEADER_FONT);
        JLabel presentLabel = createStyledLabel("Present: 0", HEADER_FONT);
        JLabel absentLabel = createStyledLabel("Absent: 0", HEADER_FONT);
        JLabel lateLabel = createStyledLabel("Late: 0", HEADER_FONT);
        JLabel percentLabel = createStyledLabel("Rate: 0%", HEADER_FONT);

        presentLabel.setForeground(SUCCESS_COLOR);
        absentLabel.setForeground(ERROR_COLOR);
        lateLabel.setForeground(WARNING_COLOR);
        percentLabel.setForeground(PRIMARY_COLOR);

        summaryPanel.add(totalLabel);
        summaryPanel.add(presentLabel);
        summaryPanel.add(absentLabel);
        summaryPanel.add(lateLabel);
        summaryPanel.add(percentLabel);

        // Load subjects
        loadSubjectsIntoCombo(subjectCombo);

        // For students, lock to their own roll number
        if ("Student".equals(currentRole)) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String sql = "SELECT student_roll FROM students WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, currentUserId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        rollField.setText(rs.getString("student_roll"));
                        rollField.setEditable(false);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Generate button action
        generateBtn.addActionListener(e -> {
            String roll = rollField.getText().trim();
            String subject = (String) subjectCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            Date from = (Date) fromDate.getValue();
            Date to = (Date) toDate.getValue();

            generateReport(reportModel, roll, subject, status, from, to,
                    totalLabel, presentLabel, absentLabel, lateLabel, percentLabel);
        });

        // Export CSV action
        exportCSVBtn.addActionListener(e -> {
            exportToCSV(reportModel, rollField.getText().trim());
        });

        // Export Text action
        exportTextBtn.addActionListener(e -> {
            exportToText(reportModel, rollField.getText().trim(),
                    totalLabel.getText(), presentLabel.getText(),
                    absentLabel.getText(), percentLabel.getText());
        });

        // Print action
        printBtn.addActionListener(e -> {
            try {
                reportTable.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Error printing: " + ex.getMessage(),
                        "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Assemble main panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(filterCard, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(reportScroll, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static void generateReport(DefaultTableModel model, String roll, String subject,
                                       String status, Date from, Date to,
                                       JLabel total, JLabel present, JLabel absent,
                                       JLabel late, JLabel percent) {
        model.setRowCount(0);

        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT s.session_date, sb.subject_name, s.session_number, " +
                            "st.first_name || ' ' || st.last_name as student_name, " +
                            "st.student_roll, a.status " +
                            "FROM attendance a " +
                            "JOIN students st ON a.student_id = st.student_id " +
                            "JOIN sessions s ON a.session_id = s.session_id " +
                            "JOIN subjects sb ON s.subject_id = sb.subject_id " +
                            "WHERE s.session_date BETWEEN ? AND ? ");

            // Only filter by roll if provided (allow empty for all students)
            if (!roll.isEmpty()) {
                sql.append("AND st.student_roll = ? ");
            }

            // Only filter by subject if a specific subject is selected (not "All Subjects")
            if (subject != null && !subject.isEmpty() && !"All Subjects".equals(subject)) {
                sql.append("AND sb.subject_name = ? ");
            }

            // Only filter by status if not "All"
            if (!"All".equals(status)) {
                sql.append("AND a.status = ? ");
            }

            sql.append("ORDER BY s.session_date DESC, st.student_roll ASC");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;

                // Date range parameters come first
                ps.setDate(paramIndex++, new java.sql.Date(from.getTime()));
                ps.setDate(paramIndex++, new java.sql.Date(to.getTime()));

                // Then optional filters
                if (!roll.isEmpty()) {
                    ps.setString(paramIndex++, roll);
                }
                if (subject != null && !subject.isEmpty() && !"All Subjects".equals(subject)) {
                    ps.setString(paramIndex++, subject);
                }
                if (!"All".equals(status)) {
                    ps.setString(paramIndex++, status);
                }

                int totalCount = 0, presentCount = 0, absentCount = 0, lateCount = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String statusStr = rs.getString("status");
                        model.addRow(new Object[]{
                                sdf.format(rs.getDate("session_date")),
                                rs.getString("subject_name"),
                                "Session " + rs.getInt("session_number"),
                                rs.getString("student_name"),
                                rs.getString("student_roll"),
                                statusStr
                        });

                        totalCount++;
                        if ("Present".equals(statusStr)) presentCount++;
                        else if ("Absent".equals(statusStr)) absentCount++;
                        else if ("Late".equals(statusStr)) lateCount++;
                    }
                }

                // Update summary
                total.setText("Total: " + totalCount);
                present.setText("Present: " + presentCount);
                absent.setText("Absent: " + absentCount);
                late.setText("Late: " + lateCount);

                if (totalCount > 0) {
                    double percentage = (presentCount * 100.0) / totalCount;
                    percent.setText(String.format("Rate: %.1f%%", percentage));
                } else {
                    percent.setText("Rate: N/A");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error generating report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void exportToCSV(DefaultTableModel model, String roll) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("attendance_report_" + roll + ".csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                for (int i = 0; i < model.getColumnCount(); i++) {
                    writer.print(model.getColumnName(i));
                    if (i < model.getColumnCount() - 1) writer.print(",");
                }
                writer.println();

                // Write data
                for (int row = 0; row < model.getRowCount(); row++) {
                    for (int col = 0; col < model.getColumnCount(); col++) {
                        Object value = model.getValueAt(row, col);
                        writer.print(value != null ? value.toString() : "");
                        if (col < model.getColumnCount() - 1) writer.print(",");
                    }
                    writer.println();
                }

                JOptionPane.showMessageDialog(null,
                        "Report exported successfully to:\n" + file.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error exporting: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void exportToText(DefaultTableModel model, String roll,
                                     String total, String present, String absent, String percent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to Text");
        fileChooser.setSelectedFile(new File("attendance_report_" + roll + ".txt"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("═══════════════════════════════════════════════════════");
                writer.println("            ATTENDANCE REPORT");
                writer.println("═══════════════════════════════════════════════════════");
                writer.println();
                writer.println("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                writer.println();
                writer.println("Summary:");
                writer.println("  " + total);
                writer.println("  " + present);
                writer.println("  " + absent);
                writer.println("  " + percent);
                writer.println();
                writer.println("───────────────────────────────────────────────────────");
                writer.println();

                // Column headers
                writer.printf("%-12s %-20s %-12s %-25s %-12s %-10s%n",
                        "Date", "Subject", "Session", "Student", "Roll No", "Status");
                writer.println("────────────────────────────────────────────────────────────────────");

                // Data rows
                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.printf("%-12s %-20s %-12s %-25s %-12s %-10s%n",
                            model.getValueAt(i, 0),
                            model.getValueAt(i, 1),
                            model.getValueAt(i, 2),
                            model.getValueAt(i, 3),
                            model.getValueAt(i, 4),
                            model.getValueAt(i, 5));
                }

                writer.println("═══════════════════════════════════════════════════════");

                JOptionPane.showMessageDialog(null,
                        "Report exported successfully to:\n" + file.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error exporting: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void loadSubjectsIntoCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        combo.addItem("All Subjects");

        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT subject_name FROM subjects ORDER BY subject_name";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    combo.addItem(rs.getString("subject_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}