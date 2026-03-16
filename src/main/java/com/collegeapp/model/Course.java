package com.collegeapp.model;

/**
 * Course entity representing a course in the Campus Management System.
 * Standalone domain entity (does not extend User).
 * Follows validation-in-setters pattern.
 */
public class Course {
    // Course attributes
    private long courseId;      // System/internal ID
    private String courseCode;  // User-visible identifier (e.g., CSE101)
    private String courseName;
    private int credits;
    private String department;

    // -------------------
    // Constructors
    // -------------------

    // Default Constructor
    public Course() {
    }

    // Parameterized Constructor
    public Course(long courseId, String courseCode, String courseName, int credits, String department) {
        this.courseId = courseId;
        setCourseCode(courseCode);
        setCourseName(courseName);
        setCredits(credits);
        setDepartment(department);
    }

    // -------------------
    // Getters & Setters
    // -------------------

    // Course ID: system/internal identifier (immutable)
    public long getCourseId() {
        return courseId;
    }

    // Course Code: non-null, non-empty, at least 2 uppercase letters followed by at least 2 digits
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        if (courseCode == null) {
            throw new IllegalArgumentException("Course code cannot be null.");
        }
        String trimmed = courseCode.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty.");
        }
        // Regex Pattern: at least 2 uppercase letters followed by at least 2 digits
        if (!trimmed.matches("^[A-Z]{2,}[0-9]{2,}$")) {
            throw new IllegalArgumentException("Course code must contain at least 2 uppercase letters followed by at least 2 digits.");
        }
        this.courseCode = trimmed;
    }

    // Course Name: letters and spaces only
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        if (courseName == null) {
            throw new IllegalArgumentException("Course name cannot be null.");
        }
        String trimmed = courseName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty.");
        }
        // Regex Pattern: letters and spaces only
        if (!trimmed.matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("Course name must contain only letters and spaces.");
        }
        this.courseName = trimmed;
    }

    // Credits: valid range (1-6)
    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        if (credits < 1 || credits > 6) {
            throw new IllegalArgumentException("Credits must be between 1 and 6.");
        }
        this.credits = credits;
    }

    // Department: letters and spaces only
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        if (department == null) {
            throw new IllegalArgumentException("Department cannot be null.");
        }
        String trimmed = department.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Department cannot be empty.");
        }
        // Regex Pattern: letters and spaces only
        if (!trimmed.matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("Department must contain only letters and spaces.");
        }
        this.department = trimmed;
    }

    // toString (do NOT include system courseId)
    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credits=" + credits +
                ", department='" + department + '\'' +
                '}';
    }
}
