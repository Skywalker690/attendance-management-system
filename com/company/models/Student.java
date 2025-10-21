package com.company.models;

/**
 * Student model representing student data
 */
public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String studentRoll;
    private String email;
    private String phone;
    private String studentClass;
    private String division;
    private Integer userId;
    
    public Student() {
    }
    
    public Student(int studentId, String firstName, String lastName, String studentRoll, 
                   String email, String phone, String studentClass, String division, Integer userId) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.studentRoll = studentRoll;
        this.email = email;
        this.phone = phone;
        this.studentClass = studentClass;
        this.division = division;
        this.userId = userId;
    }
    
    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getStudentRoll() {
        return studentRoll;
    }
    
    public void setStudentRoll(String studentRoll) {
        this.studentRoll = studentRoll;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getStudentClass() {
        return studentClass;
    }
    
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }
    
    public String getDivision() {
        return division;
    }
    
    public void setDivision(String division) {
        this.division = division;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
