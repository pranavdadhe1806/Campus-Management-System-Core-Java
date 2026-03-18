-- ============================================
-- Campus Management System — Database Schema
-- Developer: Pranav Dadhe
-- ============================================

CREATE DATABASE IF NOT EXISTS campusdb;
USE campusdb;

-- ============================================
-- TABLE 1: users
-- Authentication anchor for all roles
-- ============================================
CREATE TABLE users (
    user_id        INT           PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR(30)   NOT NULL UNIQUE,
    email          VARCHAR(100)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255)  NOT NULL,
    role           ENUM('ADMIN','FACULTY','STUDENT') NOT NULL,
    created_at     DATETIME      DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLE 2: departments
-- No FKs — must exist before students/faculty/courses
-- ============================================
CREATE TABLE departments (
    department_id  INT           PRIMARY KEY AUTO_INCREMENT,
    dept_name      VARCHAR(100)  NOT NULL UNIQUE,
    dept_code      VARCHAR(10)   NOT NULL UNIQUE
);

-- ============================================
-- TABLE 3: students
-- References users + departments
-- ============================================
CREATE TABLE students (
    student_id     INT          PRIMARY KEY AUTO_INCREMENT,
    roll_number    VARCHAR(20)  NOT NULL UNIQUE,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    mobile_no      VARCHAR(15)  UNIQUE,
    dob            DATE         NOT NULL,
    academic_year  INT          NOT NULL,
    `div`          VARCHAR(5)   NOT NULL,
    sem            INT          NOT NULL,
    user_id        INT          NOT NULL UNIQUE,
    dept_id        INT          NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (dept_id) REFERENCES departments(department_id),
    CONSTRAINT chk_academic_year CHECK (academic_year BETWEEN 1 AND 4),
    CONSTRAINT chk_sem CHECK (sem BETWEEN 1 AND 8)
);

-- ============================================
-- TABLE 4: faculty
-- References users + departments
-- ============================================
CREATE TABLE faculty (
    faculty_id     INT           PRIMARY KEY AUTO_INCREMENT,
    employee_id    VARCHAR(20)   NOT NULL UNIQUE,
    title          VARCHAR(10),
    first_name     VARCHAR(50)   NOT NULL,
    last_name      VARCHAR(50)   NOT NULL,
    designation    VARCHAR(100)  NOT NULL,
    mobile_no      VARCHAR(15)   NOT NULL UNIQUE,
    dob            DATE          NOT NULL,
    user_id        INT           NOT NULL UNIQUE,
    dept_id        INT           NOT NULL,
    FOREIGN KEY (user_id)  REFERENCES users(user_id)       ON DELETE CASCADE,
    FOREIGN KEY (dept_id)  REFERENCES departments(department_id)
);

-- ============================================
-- TABLE 5: admins
-- References users only
-- ============================================
CREATE TABLE admins (
    admin_id       INT           PRIMARY KEY AUTO_INCREMENT,
    first_name     VARCHAR(50)   NOT NULL,
    last_name      VARCHAR(50)   NOT NULL,
    mobile_no      VARCHAR(15)   NOT NULL UNIQUE,
    user_id        INT           NOT NULL UNIQUE,
    FOREIGN KEY (user_id)  REFERENCES users(user_id)       ON DELETE CASCADE
);

-- ============================================
-- TABLE 6: courses
-- References departments + faculty
-- faculty_id SET NULL on delete — course stays, just unassigned
-- ============================================
CREATE TABLE courses (
    course_id      INT           PRIMARY KEY AUTO_INCREMENT,
    course_code    VARCHAR(10)   NOT NULL UNIQUE,
    course_name    VARCHAR(100)  NOT NULL,
    credits        INT           NOT NULL,
    total_marks    INT           NOT NULL,
    lecture_hours  INT           NOT NULL,
    semester       INT           NOT NULL CHECK (semester BETWEEN 1 AND 8),
    dept_id        INT           NOT NULL,
    faculty_id     INT,
    FOREIGN KEY (dept_id)    REFERENCES departments(department_id),
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE SET NULL
);

-- ============================================
-- TABLE 7: enrollments
-- Junction table — students ↔ courses
-- Composite UNIQUE prevents duplicate enrollments
-- ============================================
CREATE TABLE enrollments (
    enrollment_id  INT           PRIMARY KEY AUTO_INCREMENT,
    student_id     INT           NOT NULL,
    course_id      INT           NOT NULL,
    status         ENUM('ACTIVE','DROPPED','COMPLETED') DEFAULT 'ACTIVE',
    enrolled_on    DATE          DEFAULT (CURRENT_DATE),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)   ON DELETE CASCADE,
    UNIQUE KEY uq_enrollment (student_id, course_id)
);