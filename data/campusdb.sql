-- ============================================
-- Campus Management System — Database Schema
-- Developer: Pranav Dadhe
-- GitHub: pranavdadhe1806
-- Version: 2.0 — March 2026
-- Total Tables: 16
-- ============================================
-- TABLE ORDER (strict dependency order):
--  1. users               ← no FK, auth anchor
--  2. departments         ← no FK, referenced by many
--  3. students            ← needs users + departments
--  4. faculty             ← needs users + departments
--  5. admins              ← needs users
--  6. courses             ← needs departments + faculty
--  7. enrollments         ← needs students + courses
--  8. otp_tokens          ← needs users
--  9. user_profiles       ← needs users
-- 10. attendance          ← needs students + courses + faculty
-- 11. exams               ← needs admins
-- 12. grading_scale       ← no FK, standalone config
-- 13. marks               ← needs exams + students + courses + faculty
-- 14. assignments         ← needs courses + faculty
-- 15. submissions         ← needs assignments + students
-- 16. assignment_comments ← needs assignments + users
-- ============================================

CREATE DATABASE IF NOT EXISTS campusdb;
USE campusdb;

-- ============================================
-- TABLE 1: users
-- Authentication anchor for all roles
-- Stores credentials only — no personal details
-- is_first_login: TRUE forces OTP password reset on first login
-- ============================================
CREATE TABLE users (
    user_id         INT           PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(30)   NOT NULL UNIQUE,
    email           VARCHAR(100)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255)  NOT NULL,
    role            ENUM('ADMIN','FACULTY','STUDENT') NOT NULL,
    is_first_login  BOOLEAN       DEFAULT TRUE,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE 2: departments
-- No foreign keys — must be created before
-- students, faculty, and courses
-- ============================================
CREATE TABLE departments (
    department_id   INT           PRIMARY KEY AUTO_INCREMENT,
    dept_name       VARCHAR(100)  NOT NULL UNIQUE,
    dept_code       VARCHAR(10)   NOT NULL UNIQUE
);

-- ============================================
-- TABLE 3: students
-- References users (CASCADE) + departments
-- roll_number: unique human visible ID, never used as FK
-- division: renamed from div — reserved keyword in MySQL
-- batch: subdivision of division e.g. A1, A2, A3
-- mobile_no: optional — email is alternative contact
-- ============================================
CREATE TABLE students (
    student_id      INT           PRIMARY KEY AUTO_INCREMENT,
    roll_number     VARCHAR(20)   NOT NULL UNIQUE,
    first_name      VARCHAR(50)   NOT NULL,
    last_name       VARCHAR(50)   NOT NULL,
    mobile_no       VARCHAR(15)   UNIQUE,
    dob             DATE          NOT NULL,
    academic_year   INT           NOT NULL,
    division        VARCHAR(5)    NOT NULL,
    batch           VARCHAR(10)   NOT NULL,
    sem             INT           NOT NULL,
    user_id         INT           NOT NULL UNIQUE,
    dept_id         INT           NOT NULL,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE,
    FOREIGN KEY (dept_id)        REFERENCES departments(department_id),
    CONSTRAINT chk_academic_year CHECK (academic_year BETWEEN 1 AND 4),
    CONSTRAINT chk_sem           CHECK (sem BETWEEN 1 AND 8)
);

-- ============================================
-- TABLE 4: faculty
-- References users (CASCADE) + departments
-- employee_id: unique human visible ID, never used as FK
-- title: optional e.g. Dr. Prof.
-- designation: mandatory e.g. Assistant Professor
-- mobile_no: mandatory for faculty — professional system
-- ============================================
CREATE TABLE faculty (
    faculty_id      INT           PRIMARY KEY AUTO_INCREMENT,
    employee_id     VARCHAR(20)   NOT NULL UNIQUE,
    title           VARCHAR(10),
    first_name      VARCHAR(50)   NOT NULL,
    last_name       VARCHAR(50)   NOT NULL,
    designation     VARCHAR(100)  NOT NULL,
    mobile_no       VARCHAR(15)   NOT NULL UNIQUE,
    dob             DATE          NOT NULL,
    user_id         INT           NOT NULL UNIQUE,
    dept_id         INT           NOT NULL,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE,
    FOREIGN KEY (dept_id)        REFERENCES departments(department_id)
);

-- ============================================
-- TABLE 5: admins
-- References users (CASCADE) only
-- Admins have no department or designation
-- mobile_no: mandatory — small admin team
-- ============================================
CREATE TABLE admins (
    admin_id        INT           PRIMARY KEY AUTO_INCREMENT,
    first_name      VARCHAR(50)   NOT NULL,
    last_name       VARCHAR(50)   NOT NULL,
    mobile_no       VARCHAR(15)   NOT NULL UNIQUE,
    user_id         INT           NOT NULL UNIQUE,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE
);

-- ============================================
-- TABLE 6: courses
-- References departments + faculty
-- faculty_id: nullable — SET NULL on faculty delete
-- Course stays in system even if faculty is removed
-- total_marks and lecture_hours defined at course level
-- ============================================
CREATE TABLE courses (
    course_id       INT           PRIMARY KEY AUTO_INCREMENT,
    course_code     VARCHAR(10)   NOT NULL UNIQUE,
    course_name     VARCHAR(100)  NOT NULL,
    credits         INT           NOT NULL,
    total_marks     INT           NOT NULL,
    lecture_hours   INT           NOT NULL,
    semester        INT           NOT NULL,
    dept_id         INT           NOT NULL,
    faculty_id      INT,
    FOREIGN KEY (dept_id)        REFERENCES departments(department_id),
    FOREIGN KEY (faculty_id)     REFERENCES faculty(faculty_id)        ON DELETE SET NULL,
    CONSTRAINT chk_course_sem    CHECK (semester BETWEEN 1 AND 8)
);

-- ============================================
-- TABLE 7: enrollments
-- Junction table — students many-to-many courses
-- Composite UNIQUE prevents duplicate enrollments
-- CASCADE on both sides — clean delete guaranteed
-- status: tracks enrollment lifecycle
-- ============================================
CREATE TABLE enrollments (
    enrollment_id   INT           PRIMARY KEY AUTO_INCREMENT,
    student_id      INT           NOT NULL,
    course_id       INT           NOT NULL,
    status          ENUM('ACTIVE','DROPPED','COMPLETED')    DEFAULT 'ACTIVE',
    enrolled_on     DATE          DEFAULT (CURRENT_DATE),
    FOREIGN KEY (student_id)     REFERENCES students(student_id)      ON DELETE CASCADE,
    FOREIGN KEY (course_id)      REFERENCES courses(course_id)        ON DELETE CASCADE,
    UNIQUE KEY uq_enrollment (student_id, course_id)
);

-- ============================================
-- TABLE 8: otp_tokens
-- Temporary OTP storage for first time login flow
-- Verified OTPs: deleted instantly after verification
-- Expired OTPs: deleted after 10 minutes automatically
-- No row ever lives longer than 10 minutes
-- created_at: used to prevent OTP spam (1 min cooldown)
-- ============================================
CREATE TABLE otp_tokens (
    otp_id          INT           PRIMARY KEY AUTO_INCREMENT,
    user_id         INT           NOT NULL,
    otp_code        VARCHAR(6)    NOT NULL,
    otp_expires_at  DATETIME      NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE
);

-- ============================================
-- TABLE 9: user_profiles
-- Optional extended profile for all roles
-- profile_picture: file path stored, image on disk
-- address fields: all optional — complete your profile section
-- One profile per user — UNIQUE on user_id
-- updated_at: auto updates on every profile change
-- ============================================
CREATE TABLE user_profiles (
    profile_id      INT           PRIMARY KEY AUTO_INCREMENT,
    user_id         INT           NOT NULL UNIQUE,
    profile_picture VARCHAR(500),
    bio             TEXT,
    address_line1   VARCHAR(200),
    address_line2   VARCHAR(200),
    city            VARCHAR(100),
    state           VARCHAR(100),
    pincode         VARCHAR(10),
    country         VARCHAR(100)  DEFAULT 'India',
    updated_at      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE
);

-- ============================================
-- TABLE 10: attendance
-- Per lecture attendance marked by faculty
-- One row = one student in one course on one date
-- Composite UNIQUE prevents duplicate entries
-- status: PRESENT, ABSENT, LATE
-- Percentage calculated in Java — never stored
-- ============================================
CREATE TABLE attendance (
    attendance_id   INT           PRIMARY KEY AUTO_INCREMENT,
    student_id      INT           NOT NULL,
    course_id       INT           NOT NULL,
    faculty_id      INT           NOT NULL,
    date            DATE          NOT NULL,
    status          ENUM('PRESENT','ABSENT','LATE')         NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)     REFERENCES students(student_id)      ON DELETE CASCADE,
    FOREIGN KEY (course_id)      REFERENCES courses(course_id)        ON DELETE CASCADE,
    FOREIGN KEY (faculty_id)     REFERENCES faculty(faculty_id)       ON DELETE CASCADE,
    UNIQUE KEY uq_attendance (student_id, course_id, date)
);

-- ============================================
-- TABLE 11: exams
-- Created by admin only
-- Tied to a specific semester and academic year
-- exam_type: generic e.g. Mid Sem, End Sem, Internal
-- Faculty enters marks under this exam for their subject
-- ============================================
CREATE TABLE exams (
    exam_id         INT           PRIMARY KEY AUTO_INCREMENT,
    exam_name       VARCHAR(100)  NOT NULL,
    exam_type       VARCHAR(50)   NOT NULL,
    semester        INT           NOT NULL,
    academic_year   INT           NOT NULL,
    created_by      INT           NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by)     REFERENCES admins(admin_id),
    CONSTRAINT chk_exam_sem      CHECK (semester BETWEEN 1 AND 8),
    CONSTRAINT chk_exam_year     CHECK (academic_year BETWEEN 1 AND 4)
);

-- ============================================
-- TABLE 12: grading_scale
-- No foreign keys — standalone config table
-- Configurable by admin — any college sets their own
-- grade_points: DECIMAL to support values like 9.5
-- Example: 90-100 = O grade = 10 points
-- ============================================
CREATE TABLE grading_scale (
    grade_id        INT           PRIMARY KEY AUTO_INCREMENT,
    min_marks       INT           NOT NULL,
    max_marks       INT           NOT NULL,
    grade           VARCHAR(5)    NOT NULL,
    grade_points    DECIMAL(3,1)  NOT NULL
);

-- ============================================
-- TABLE 13: marks
-- Entered by faculty for their subject under an exam
-- One row = one student, one course, one exam
-- Composite UNIQUE prevents duplicate mark entries
-- SGPA, CGPA, grades calculated in Java — never stored
-- ============================================
CREATE TABLE marks (
    mark_id         INT           PRIMARY KEY AUTO_INCREMENT,
    exam_id         INT           NOT NULL,
    student_id      INT           NOT NULL,
    course_id       INT           NOT NULL,
    faculty_id      INT           NOT NULL,
    marks_obtained  INT           NOT NULL,
    total_marks     INT           NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id)        REFERENCES exams(exam_id)            ON DELETE CASCADE,
    FOREIGN KEY (student_id)     REFERENCES students(student_id)      ON DELETE CASCADE,
    FOREIGN KEY (course_id)      REFERENCES courses(course_id)        ON DELETE CASCADE,
    FOREIGN KEY (faculty_id)     REFERENCES faculty(faculty_id)       ON DELETE CASCADE,
    UNIQUE KEY uq_marks (exam_id, student_id, course_id)
);

-- ============================================
-- TABLE 14: assignments
-- Created by faculty for their course
-- Can target entire division or specific batch
-- division NULL + batch NULL = entire course
-- division set + batch NULL = entire division
-- division set + batch set = specific batch only
-- deadline: faculty sets submission deadline
-- ============================================
CREATE TABLE assignments (
    assignment_id   INT           PRIMARY KEY AUTO_INCREMENT,
    course_id       INT           NOT NULL,
    faculty_id      INT           NOT NULL,
    title           VARCHAR(200)  NOT NULL,
    description     TEXT,
    division        VARCHAR(5),
    batch           VARCHAR(10),
    deadline        DATETIME      NOT NULL,
    total_marks     INT           NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id)      REFERENCES courses(course_id)        ON DELETE CASCADE,
    FOREIGN KEY (faculty_id)     REFERENCES faculty(faculty_id)       ON DELETE CASCADE
);

-- ============================================
-- TABLE 15: submissions
-- Student submits file for an assignment
-- file_path: path to file on disk — not stored in DB
-- marks_obtained: filled by faculty after grading
-- graded_at: timestamp when faculty graded submission
-- Composite UNIQUE prevents duplicate submissions
-- ============================================
CREATE TABLE submissions (
    submission_id   INT           PRIMARY KEY AUTO_INCREMENT,
    assignment_id   INT           NOT NULL,
    student_id      INT           NOT NULL,
    file_path       VARCHAR(500)  NOT NULL,
    comment         TEXT,
    marks_obtained  INT,
    graded_at       DATETIME,
    submitted_at    DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id)  REFERENCES assignments(assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id)     REFERENCES students(student_id)       ON DELETE CASCADE,
    UNIQUE KEY uq_submission (assignment_id, student_id)
);

-- ============================================
-- TABLE 16: assignment_comments
-- Comments on assignments by any user
-- Both faculty and students can comment
-- user_id references users — works for all roles
-- ============================================
CREATE TABLE assignment_comments (
    comment_id      INT           PRIMARY KEY AUTO_INCREMENT,
    assignment_id   INT           NOT NULL,
    user_id         INT           NOT NULL,
    comment         TEXT          NOT NULL,
    created_at      DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assignment_id)  REFERENCES assignments(assignment_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)        REFERENCES users(user_id)             ON DELETE CASCADE
);
