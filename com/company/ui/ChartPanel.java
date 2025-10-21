package com.company.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Map;
import java.util.LinkedHashMap;

import static com.company.ui.UIConstants.*;

/**
 * Custom panel for drawing charts and visualizations
 */
public class ChartPanel extends JPanel {
    
    /**
     * Create a bar chart panel
     */
    public static JPanel createBarChart(String title, Map<String, Integer> data, Color barColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (data == null || data.isEmpty()) {
                    g2d.setFont(NORMAL_FONT);
                    g2d.setColor(TEXT_SECONDARY);
                    String msg = "No data available";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(msg)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(msg, x, y);
                    return;
                }
                
                int padding = 40;
                int labelPadding = 20;
                int chartWidth = getWidth() - 2 * padding;
                int chartHeight = getHeight() - 2 * padding;
                
                // Find max value for scaling
                int maxValue = data.values().stream().max(Integer::compareTo).orElse(1);
                if (maxValue == 0) maxValue = 1;
                
                // Draw bars
                int barCount = data.size();
                int barWidth = Math.max(30, (chartWidth - (barCount - 1) * 10) / barCount);
                int x = padding;
                
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    int barHeight = (int) ((double) entry.getValue() / maxValue * (chartHeight - labelPadding));
                    int y = padding + (chartHeight - labelPadding - barHeight);
                    
                    // Draw bar
                    g2d.setColor(barColor);
                    g2d.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 5, 5));
                    
                    // Draw value on top of bar
                    g2d.setColor(TEXT_PRIMARY);
                    g2d.setFont(SMALL_FONT);
                    String valueStr = String.valueOf(entry.getValue());
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(valueStr, x + (barWidth - fm.stringWidth(valueStr)) / 2, y - 5);
                    
                    // Draw label below bar
                    String label = entry.getKey();
                    if (label.length() > 8) label = label.substring(0, 7) + "..";
                    g2d.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, 
                                 padding + chartHeight - labelPadding + 15);
                    
                    x += barWidth + 10;
                }
            }
        };
        
        panel.setBackground(CARD_BACKGROUND);
        panel.setPreferredSize(new Dimension(500, 300));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            title, 
            0, 
            0, 
            NORMAL_FONT, 
            TEXT_PRIMARY));
        
        return panel;
    }
    
    /**
     * Create a pie chart panel
     */
    public static JPanel createPieChart(String title, Map<String, Integer> data) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (data == null || data.isEmpty()) {
                    g2d.setFont(NORMAL_FONT);
                    g2d.setColor(TEXT_SECONDARY);
                    String msg = "No data available";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(msg)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(msg, x, y);
                    return;
                }
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = Math.min(getWidth(), getHeight()) / 3;
                
                // Calculate total
                int total = data.values().stream().mapToInt(Integer::intValue).sum();
                if (total == 0) {
                    g2d.setFont(NORMAL_FONT);
                    g2d.setColor(TEXT_SECONDARY);
                    String msg = "No data";
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(msg)) / 2;
                    int y = getHeight() / 2;
                    g2d.drawString(msg, x, y);
                    return;
                }
                
                Color[] colors = {PRIMARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, ERROR_COLOR, ACCENT_COLOR};
                int colorIndex = 0;
                double currentAngle = 0;
                
                // Draw pie slices
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    double angle = (entry.getValue() * 360.0) / total;
                    
                    g2d.setColor(colors[colorIndex % colors.length]);
                    g2d.fill(new Arc2D.Double(
                        centerX - radius, centerY - radius,
                        radius * 2, radius * 2,
                        currentAngle, angle, Arc2D.PIE));
                    
                    currentAngle += angle;
                    colorIndex++;
                }
                
                // Draw legend
                int legendX = 10;
                int legendY = getHeight() - data.size() * 20 - 10;
                colorIndex = 0;
                
                g2d.setFont(SMALL_FONT);
                for (Map.Entry<String, Integer> entry : data.entrySet()) {
                    g2d.setColor(colors[colorIndex % colors.length]);
                    g2d.fillRect(legendX, legendY, 15, 15);
                    
                    g2d.setColor(TEXT_PRIMARY);
                    double percentage = (entry.getValue() * 100.0) / total;
                    String label = String.format("%s: %d (%.1f%%)", entry.getKey(), entry.getValue(), percentage);
                    g2d.drawString(label, legendX + 20, legendY + 12);
                    
                    legendY += 20;
                    colorIndex++;
                }
            }
        };
        
        panel.setBackground(CARD_BACKGROUND);
        panel.setPreferredSize(new Dimension(400, 300));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR), 
            title, 
            0, 
            0, 
            NORMAL_FONT, 
            TEXT_PRIMARY));
        
        return panel;
    }
    
    /**
     * Create a progress bar with label
     */
    public static JPanel createProgressBar(String label, double percentage) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(CARD_BACKGROUND);
        
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(SMALL_FONT);
        nameLabel.setForeground(TEXT_PRIMARY);
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) percentage);
        progressBar.setStringPainted(true);
        progressBar.setString(String.format("%.1f%%", percentage));
        progressBar.setFont(SMALL_FONT);
        
        // Color based on percentage
        if (percentage >= 90) {
            progressBar.setForeground(SUCCESS_COLOR);
        } else if (percentage >= 75) {
            progressBar.setForeground(PRIMARY_COLOR);
        } else if (percentage >= 60) {
            progressBar.setForeground(WARNING_COLOR);
        } else {
            progressBar.setForeground(ERROR_COLOR);
        }
        
        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
}
