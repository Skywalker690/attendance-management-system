-- PostgreSQL Database Schema for Student Attendance Management System
-- Database: student_attendance_db

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS attendance CASCADE;
DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS teachers CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table
CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL CHECK (role IN ('Admin', 'Teacher', 'Student')),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create students table
CREATE TABLE students (
                          student_id SERIAL PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          student_roll VARCHAR(20) UNIQUE NOT NULL,
                          email VARCHAR(100),
                          phone VARCHAR(20),
                          photo_path VARCHAR(255),
                          class VARCHAR(10) CHECK (class IN ('S1', 'S2', 'S3', 'S4', 'S5', 'S6', 'S7', 'S8')),
                          division VARCHAR(1) CHECK (division IN ('A', 'B', 'C', 'D', 'E')),
                          user_id INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create teachers table
CREATE TABLE teachers (
                          teacher_id SERIAL PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50) NOT NULL,
                          email VARCHAR(100) UNIQUE NOT NULL,
                          phone VARCHAR(20),
                          user_id INTEGER REFERENCES users(user_id) ON DELETE SET NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create subjects table
CREATE TABLE subjects (
                          subject_id SERIAL PRIMARY KEY,
                          subject_name VARCHAR(100) UNIQUE NOT NULL,
                          subject_code VARCHAR(20),
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create sessions table
CREATE TABLE sessions (
                          session_id SERIAL PRIMARY KEY,
                          session_date DATE NOT NULL,
                          subject_id INTEGER NOT NULL REFERENCES subjects(subject_id) ON DELETE CASCADE,
                          teacher_id INTEGER REFERENCES teachers(teacher_id) ON DELETE SET NULL,
                          session_number INTEGER NOT NULL CHECK (session_number >= 1 AND session_number <= 10),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE(session_date, subject_id, session_number)
);

-- Create attendance table
CREATE TABLE attendance (
                            attendance_id SERIAL PRIMARY KEY,
                            student_id INTEGER NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
                            session_id INTEGER NOT NULL REFERENCES sessions(session_id) ON DELETE CASCADE,
                            status VARCHAR(20) NOT NULL CHECK (status IN ('Present', 'Absent', 'Late', 'Excused')),
                            marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            UNIQUE(student_id, session_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_students_roll ON students(student_roll);
CREATE INDEX idx_students_user_id ON students(user_id);
CREATE INDEX idx_teachers_email ON teachers(email);
CREATE INDEX idx_sessions_date ON sessions(session_date);
CREATE INDEX idx_sessions_subject ON sessions(subject_id);
CREATE INDEX idx_attendance_student ON attendance(student_id);
CREATE INDEX idx_attendance_session ON attendance(session_id);
CREATE INDEX idx_attendance_status ON attendance(status);

----------------------Login--------------------------------

-- Insert default admin user (password: 123)
INSERT INTO users (username, password, role) VALUES
    ('admin', '123', 'Admin');

-- Insert sample teacher users (password: 123)
INSERT INTO users (username, password, role) VALUES
                                                 ('teacher1', '123', 'Teacher'),
                                                 ('teacher2', '123', 'Teacher'),
                                                 ('teacher3', '123', 'Teacher'),
                                                 ('teacher4', '123', 'Teacher'),
                                                 ('teacher5', '123', 'Teacher');

-- Insert sample student user (password: 123)
INSERT INTO users (username, password, role) VALUES
    ('student1', '123', 'Student');

------------------------------------------------------------


-- Insert sample subjects
INSERT INTO subjects (subject_name, subject_code) VALUES
                                                      ('Mathematics', 'MATH101'),
                                                      ('Physics', 'PHYS101'),
                                                      ('Computer Science', 'CS101'),
                                                      ('English', 'ENG101'),
                                                      ('Chemistry', 'CHEM101');

-- Insert sample teachers
INSERT INTO teachers (first_name, last_name, email, user_id) VALUES
                                                                 ('John', 'Doe', 'john.doe@school.edu', 2),
                                                                 ('Jane', 'Smith', 'jane.smith@school.edu', 3),
                                                                 ('Michael', 'Brown', 'michael.brown@school.edu', 4),
                                                                 ('Emily', 'Johnson', 'emily.johnson@school.edu', 5),
                                                                 ('David', 'Wilson', 'david.wilson@school.edu', 6);

-- Insert sample students
INSERT INTO students (first_name, last_name, student_roll, email, class, division, user_id) VALUES
                                                                               ('Alice', 'Smith', 'STU001', 'alice.smith@student.edu', 'S1', 'A', 7),
                                                                               ('Bob', 'Johnson', 'STU002', 'bob.johnson@student.edu', 'S1', 'A', NULL),
                                                                               ('Charlie', 'Brown', 'STU003', 'charlie.brown@student.edu', 'S1', 'B', NULL),
                                                                               ('Diana', 'Wilson', 'STU004', 'diana.wilson@student.edu', 'S1', 'B', NULL),
                                                                               ('Eve', 'Davis', 'STU005', 'eve.davis@student.edu', 'S2', 'A', NULL),
                                                                               ('Frank', 'Taylor', 'STU006', 'frank.taylor@student.edu', 'S2', 'A', NULL),
                                                                               ('Grace', 'Lee', 'STU007', 'grace.lee@student.edu', 'S2', 'B', NULL),
                                                                               ('Henry', 'Martinez', 'STU008', 'henry.martinez@student.edu', 'S2', 'B', NULL),
                                                                               ('Isabella', 'Garcia', 'STU009', 'isabella.garcia@student.edu', 'S3', 'A', NULL),
                                                                               ('James', 'Lopez', 'STU010', 'james.lopez@student.edu', 'S3', 'A', NULL),
                                                                               ('Kelly', 'Hernandez', 'STU011', 'kelly.hernandez@student.edu', 'S3', 'B', NULL),
                                                                               ('Liam', 'Moore', 'STU012', 'liam.moore@student.edu', 'S3', 'B', NULL),
                                                                               ('Mia', 'Clark', 'STU013', 'mia.clark@student.edu', 'S4', 'A', NULL),
                                                                               ('Noah', 'Lewis', 'STU014', 'noah.lewis@student.edu', 'S4', 'A', NULL),
                                                                               ('Olivia', 'Walker', 'STU015', 'olivia.walker@student.edu', 'S4', 'B', NULL),
                                                                               ('Peter', 'Hall', 'STU016', 'peter.hall@student.edu', 'S4', 'B', NULL),
                                                                               ('Quinn', 'Allen', 'STU017', 'quinn.allen@student.edu', 'S5', 'A', NULL),
                                                                               ('Rachel', 'Young', 'STU018', 'rachel.young@student.edu', 'S5', 'A', NULL),
                                                                               ('Samuel', 'King', 'STU019', 'samuel.king@student.edu', 'S5', 'B', NULL),
                                                                               ('Tara', 'Scott', 'STU020', 'tara.scott@student.edu', 'S5', 'B', NULL);