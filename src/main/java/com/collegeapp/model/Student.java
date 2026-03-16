package com.collegeapp.model;

/**
 * Student entity representing a student user in the Campus Management System.
 * Extends User with student-specific attributes (rollNumber, department, year).
 * Follows validation-in-setters pattern.
 */
public class Student extends User {
    // Student-specific attributes
    private String rollNumber;  // Student-visible identifier (NOT system id)
    private String department;
    private int year;

    // -------------------
    // Constructors
    // -------------------

    // Default Constructor
    public Student() {
        super();
    }

    // Parameterized Constructor
    public Student(long id, String name, String email, String password,
                   String rollNumber, String department, int year) {
        super(id, name, email, password, "STUDENT");
        setRollNumber(rollNumber);
        setDepartment(department);
        setYear(year);
    }

    // -------------------
    // Abstract Method Implementation
    // -------------------

    @Override
    public void displayDetails() {
        System.out.println("========== Student Details ==========");
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Department: " + department);
        System.out.println("Year: " + year);
        System.out.println("Role: " + getRole());
        System.out.println("=====================================");
    }

    // -------------------
    // Getters & Setters
    // -------------------

    // Roll Number: non-null, non-empty, alphanumeric only
    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        if (rollNumber == null) {
            throw new IllegalArgumentException("Roll number cannot be null.");
        }
        String trimmed = rollNumber.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Roll number cannot be empty.");
        }
        // Regex Pattern: alphanumeric only
        if (!trimmed.matches("^[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException("Roll number must contain only alphanumeric characters.");
        }
        this.rollNumber = trimmed;
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

    // Year: valid academic year (1-4)
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year < 1 || year > 4) {
            throw new IllegalArgumentException("Year must be between 1 and 4.");
        }
        this.year = year;
    }

    // toString (do NOT print password)
    @Override
    public String toString() {
        return "Student{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", rollNumber='" + rollNumber + '\'' +
                ", department='" + department + '\'' +
                ", year=" + year +
                ", role=" + getRole() +
                '}';
    }
}