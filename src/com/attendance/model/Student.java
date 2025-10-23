package com.attendance.model;

import java.io.Serializable;


public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String name;
    private String email;
    private String course;
    
    public Student(String studentId, String name, String email, String course) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.course = course;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "ID='" + studentId + '\'' +
                ", Name='" + name + '\'' +
                ", Email='" + email + '\'' +
                ", Course='" + course + '\'' +
                '}';
    }
}
