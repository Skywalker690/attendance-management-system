package com.attendance.model;

import java.io.Serializable;
import java.time.LocalDate;


public class AttendanceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private LocalDate date;
    private boolean present;
    
    public AttendanceRecord(String studentId, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.date = date;
        this.present = present;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public boolean isPresent() {
        return present;
    }
    
    public void setPresent(boolean present) {
        this.present = present;
    }
    
    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "StudentID='" + studentId + '\'' +
                ", Date=" + date +
                ", Status=" + (present ? "Present" : "Absent") +
                '}';
    }
}
