package com.attendance.manager;

import com.attendance.model.Student;
import com.attendance.model.AttendanceRecord;

import java.time.LocalDate;
import java.util.*;

/**
 * HashMap: For storing students with unique IDs
 * ArrayList: For maintaining attendance logs dynamically
 * Queue: For managing date-wise attendance sessions
 */
public class AttendanceManager {
    
    // HashMap to store students with studentId as key
    private HashMap<String, Student> students;
    
    // ArrayList to store all attendance records
    private ArrayList<AttendanceRecord> attendanceRecords;
    
    // Queue to manage date-wise attendance sessions (FIFO)
    private Queue<LocalDate> attendanceSessions;
    
    public AttendanceManager() {
        this.students = new HashMap<>();
        this.attendanceRecords = new ArrayList<>();
        this.attendanceSessions = new LinkedList<>();
    }

    public boolean addStudent(Student student) {
        if (students.containsKey(student.getStudentId())) {
            return false; // Student already exists
        }
        students.put(student.getStudentId(), student);
        return true;
    }
    

    public boolean removeStudent(String studentId) {
        if (!students.containsKey(studentId)) {
            return false;
        }
        students.remove(studentId);
        // Also remove all attendance records for this student
        attendanceRecords.removeIf(record -> record.getStudentId().equals(studentId));
        return true;
    }

    public Student getStudent(String studentId) {
        return students.get(studentId);
    }
    

    public Collection<Student> getAllStudents() {
        return students.values();
    }
    

    public boolean markAttendance(String studentId, LocalDate date, boolean present) {
        if (!students.containsKey(studentId)) {
            return false; // Student doesn't exist
        }
        
        // Check if attendance already exists for this student on this date
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getStudentId().equals(studentId) && record.getDate().equals(date)) {
                // Update existing record
                record.setPresent(present);
                return true;
            }
        }
        
        // Add new attendance record
        AttendanceRecord record = new AttendanceRecord(studentId, date, present);
        attendanceRecords.add(record);
        
        // Add date to sessions queue if not already present
        if (!attendanceSessions.contains(date)) {
            attendanceSessions.offer(date);
        }
        
        return true;
    }
    
    /**
     * Get all attendance records for a specific student
     */
    public ArrayList<AttendanceRecord> getStudentAttendance(String studentId) {
        ArrayList<AttendanceRecord> studentRecords = new ArrayList<>();
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getStudentId().equals(studentId)) {
                studentRecords.add(record);
            }
        }
        return studentRecords;
    }
    

    public ArrayList<AttendanceRecord> getAttendanceByDate(LocalDate date) {
        ArrayList<AttendanceRecord> dateRecords = new ArrayList<>();
        for (AttendanceRecord record : attendanceRecords) {
            if (record.getDate().equals(date)) {
                dateRecords.add(record);
            }
        }
        return dateRecords;
    }
    

    public double calculateAttendancePercentage(String studentId) {
        ArrayList<AttendanceRecord> studentRecords = getStudentAttendance(studentId);
        
        if (studentRecords.isEmpty()) {
            return 0.0;
        }
        
        int presentCount = 0;
        for (AttendanceRecord record : studentRecords) {
            if (record.isPresent()) {
                presentCount++;
            }
        }
        
        return (presentCount * 100.0) / studentRecords.size();
    }

    public Map<String, Integer> getStudentStatistics(String studentId) {
        ArrayList<AttendanceRecord> studentRecords = getStudentAttendance(studentId);
        
        int totalDays = studentRecords.size();
        int presentDays = 0;
        int absentDays = 0;
        
        for (AttendanceRecord record : studentRecords) {
            if (record.isPresent()) {
                presentDays++;
            } else {
                absentDays++;
            }
        }
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", totalDays);
        stats.put("present", presentDays);
        stats.put("absent", absentDays);
        
        return stats;
    }
    

    public Queue<LocalDate> getAttendanceSessions() {
        return new LinkedList<>(attendanceSessions);
    }
    

    public ArrayList<AttendanceRecord> getAllAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }
    

    public int getTotalStudents() {
        return students.size();
    }

    public boolean studentExists(String studentId) {
        return students.containsKey(studentId);
    }
}
