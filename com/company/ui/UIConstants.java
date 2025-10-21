package com.company.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Modern UI constants and styling for the application
 */
public class UIConstants {
    
    // Modern Color Palette - Material Design inspired
    public static final Color PRIMARY_COLOR = new Color(63, 81, 181);        // Indigo
    public static final Color PRIMARY_DARK = new Color(48, 63, 159);         // Dark Indigo
    public static final Color PRIMARY_LIGHT = new Color(92, 107, 192);       // Light Indigo
    public static final Color ACCENT_COLOR = new Color(255, 64, 129);        // Pink Accent
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);        // Green
    public static final Color WARNING_COLOR = new Color(255, 152, 0);        // Orange
    public static final Color ERROR_COLOR = new Color(244, 67, 54);          // Red
    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250);   // Light Gray
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    public static final Color BORDER_COLOR = new Color(224, 224, 224);
    public static final Color TABLE_HEADER_BG = new Color(245, 245, 245);
    public static final Color TABLE_ALTERNATE_ROW = new Color(250, 250, 250);
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Dimensions
    public static final int PADDING = 15;
    public static final int BORDER_RADIUS = 8;
    public static final int BUTTON_HEIGHT = 40;
    
    /**
     * Create a modern styled button
     */
    public static JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, BUTTON_HEIGHT));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    /**
     * Create a primary action button
     */
    public static JButton createPrimaryButton(String text) {
        return createModernButton(text, PRIMARY_COLOR);
    }
    
    /**
     * Create a success action button
     */
    public static JButton createSuccessButton(String text) {
        return createModernButton(text, SUCCESS_COLOR);
    }
    
    /**
     * Create a danger action button
     */
    public static JButton createDangerButton(String text) {
        return createModernButton(text, ERROR_COLOR);
    }
    
    /**
     * Create a warning action button
     */
    public static JButton createWarningButton(String text) {
        return createModernButton(text, WARNING_COLOR);
    }
    
    /**
     * Create a styled text field
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    /**
     * Create a styled password field
     */
    public static JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(NORMAL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    /**
     * Create a styled combo box
     */
    public static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(NORMAL_FONT);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }
    
    /**
     * Create a styled label
     */
    public static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Create a card panel with shadow effect
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        return panel;
    }
    
    /**
     * Style a table with modern appearance
     */
    public static void styleTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header styling
        table.getTableHeader().setFont(HEADER_FONT);
        table.getTableHeader().setBackground(TABLE_HEADER_BG);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALTERNATE_ROW);
                }
                return c;
            }
        });
    }
    
    /**
     * Create a title panel
     */
    public static JPanel createTitlePanel(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(NORMAL_FONT);
        subtitleLabel.setForeground(new Color(255, 255, 255, 180));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        panel.add(textPanel, BorderLayout.WEST);
        
        return panel;
    }
}
