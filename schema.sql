-- ==========================================================
-- Student Attendance Management System (Sync Schema Script)
-- Safe to run multiple times (idempotent)
-- Author: Sanjo
-- ==========================================================

-- ===================
-- 1. USERS TABLE
-- ===================
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(20) NOT NULL CHECK (role IN ('Admin', 'Teacher', 'Student')),
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===================
-- 2. STUDENTS TABLE
-- ===================
CREATE TABLE IF NOT EXISTS students (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100),
                                        first_name VARCHAR(50),
                                        last_name VARCHAR(50),
                                        roll_number VARCHAR(20) UNIQUE,
                                        department VARCHAR(50),
                                        class VARCHAR(10) CHECK (class IN ('S1','S2','S3','S4','S5','S6','S7','S8')),
                                        division VARCHAR(1) CHECK (division IN ('A','B','C','D','E')),
                                        email VARCHAR(100),
                                        phone VARCHAR(20),
                                        photo_path VARCHAR(255),
                                        year INT,
                                        user_id INT UNIQUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add missing columns safely
ALTER TABLE students ADD COLUMN IF NOT EXISTS roll_number VARCHAR(20) UNIQUE;
ALTER TABLE students ADD COLUMN IF NOT EXISTS department VARCHAR(50);
ALTER TABLE students ADD COLUMN IF NOT EXISTS year INT;
ALTER TABLE students ADD COLUMN IF NOT EXISTS user_id INT UNIQUE;
ALTER TABLE students ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE students ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE students ADD COLUMN IF NOT EXISTS photo_path VARCHAR(255);
ALTER TABLE students ADD COLUMN IF NOT EXISTS class VARCHAR(10);
ALTER TABLE students ADD COLUMN IF NOT EXISTS division VARCHAR(1);
ALTER TABLE students ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add FK constraint to users
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name='students' AND constraint_type='FOREIGN KEY'
        ) THEN
            ALTER TABLE students
                ADD CONSTRAINT fk_students_user FOREIGN KEY (user_id)
                    REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END $$;

-- ===================
-- 3. TEACHERS TABLE
-- ===================
CREATE TABLE IF NOT EXISTS teachers (
                                        id SERIAL PRIMARY KEY,
                                        first_name VARCHAR(50),
                                        last_name VARCHAR(50),
                                        name VARCHAR(100),
                                        email VARCHAR(100) UNIQUE,
                                        phone VARCHAR(20),
                                        subject VARCHAR(100),
                                        user_id INT UNIQUE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE teachers ADD COLUMN IF NOT EXISTS name VARCHAR(100);
ALTER TABLE teachers ADD COLUMN IF NOT EXISTS subject VARCHAR(100);
ALTER TABLE teachers ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE teachers ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE teachers ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name='teachers' AND constraint_type='FOREIGN KEY'
        ) THEN
            ALTER TABLE teachers
                ADD CONSTRAINT fk_teachers_user FOREIGN KEY (user_id)
                    REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END $$;

-- ===================
-- 4. SUBJECTS TABLE
-- ===================
CREATE TABLE IF NOT EXISTS subjects (
                                        id SERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        code VARCHAR(20) UNIQUE NOT NULL,
                                        description TEXT,
                                        teacher_id INT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE subjects ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name='subjects' AND constraint_type='FOREIGN KEY'
        ) THEN
            ALTER TABLE subjects
                ADD CONSTRAINT fk_subjects_teacher FOREIGN KEY (teacher_id)
                    REFERENCES teachers(id) ON DELETE SET NULL;
        END IF;
    END $$;

-- ===================
-- 5. SESSIONS TABLE
-- ===================
CREATE TABLE IF NOT EXISTS sessions (
                                        id SERIAL PRIMARY KEY,
                                        subject_id INT NOT NULL,
                                        session_date DATE NOT NULL,
                                        session_number INT NOT NULL CHECK (session_number >= 1 AND session_number <= 10),
                                        teacher_id INT,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE sessions ADD COLUMN IF NOT EXISTS session_number INT NOT NULL DEFAULT 1 CHECK (session_number >= 1 AND session_number <= 10);
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS teacher_id INT;
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name='sessions' AND constraint_type='FOREIGN KEY'
        ) THEN
            ALTER TABLE sessions
                ADD CONSTRAINT fk_sessions_subject FOREIGN KEY (subject_id)
                    REFERENCES subjects(id) ON DELETE CASCADE;
        END IF;
    END $$;

-- Unique constraint for (session_date, subject_id, session_number)
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'sessions_session_date_subject_id_session_number_key'
        ) THEN
            ALTER TABLE sessions
                ADD CONSTRAINT sessions_session_date_subject_id_session_number_key
                    UNIQUE (session_date, subject_id, session_number);
        END IF;
    END $$;

-- ===================
-- 6. ATTENDANCE TABLE
-- ===================
CREATE TABLE IF NOT EXISTS attendance (
                                          id SERIAL PRIMARY KEY,
                                          student_id INT NOT NULL,
                                          session_id INT NOT NULL,
                                          status VARCHAR(20) NOT NULL CHECK (status IN ('Present', 'Absent', 'Late', 'Excused')),
                                          marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE attendance ADD COLUMN IF NOT EXISTS marked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE attendance ADD COLUMN IF NOT EXISTS status VARCHAR(20) CHECK (status IN ('Present', 'Absent', 'Late', 'Excused'));

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints
            WHERE table_name='attendance' AND constraint_type='FOREIGN KEY'
        ) THEN
            ALTER TABLE attendance
                ADD CONSTRAINT fk_attendance_student FOREIGN KEY (student_id)
                    REFERENCES students(id) ON DELETE CASCADE;
            ALTER TABLE attendance
                ADD CONSTRAINT fk_attendance_session FOREIGN KEY (session_id)
                    REFERENCES sessions(id) ON DELETE CASCADE;
        END IF;
    END $$;

-- ===================
-- 7. INDEXES
-- ===================
CREATE INDEX IF NOT EXISTS idx_students_roll ON students(roll_number);
CREATE INDEX IF NOT EXISTS idx_students_user_id ON students(user_id);
CREATE INDEX IF NOT EXISTS idx_teachers_email ON teachers(email);
CREATE INDEX IF NOT EXISTS idx_sessions_date ON sessions(session_date);
CREATE INDEX IF NOT EXISTS idx_sessions_subject ON sessions(subject_id);
CREATE INDEX IF NOT EXISTS idx_attendance_student ON attendance(student_id);
CREATE INDEX IF NOT EXISTS idx_attendance_session ON attendance(session_id);
CREATE INDEX IF NOT EXISTS idx_attendance_status ON attendance(status);

-- ===================
-- 8. DEFAULT DATA
-- ===================
INSERT INTO users (username, password, role)
SELECT 'admin', '123', 'Admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username='admin');

COMMIT;

-- ==========================================================
-- ✅ Schema Sync Completed Successfully
-- ==========================================================
