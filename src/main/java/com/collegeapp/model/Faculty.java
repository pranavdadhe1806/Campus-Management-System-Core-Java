package com.collegeapp.model;

/**
 * Faculty entity representing a faculty user in the Campus Management System.
 * Extends User with faculty-specific attributes (employeeId, department, designation).
 * Follows validation-in-setters pattern.
 */
public class Faculty extends User {
    // Faculty-specific attributes
    private String employeeId;  // Faculty-visible identifier (NOT system id)
    private String department;
    private String designation;

    // -------------------
    // Constructors
    // -------------------

    // Default Constructor
    public Faculty() {
        super();
    }

    // Parameterized Constructor
    public Faculty(long id, String name, String email, String password,
                   String employeeId, String department, String designation) {
        super(id, name, email, password, "FACULTY");
        setEmployeeId(employeeId);
        setDepartment(department);
        setDesignation(designation);
    }

    // -------------------
    // Abstract Method Implementation
    // -------------------

    @Override
    public void displayDetails() {
        System.out.println("========== Faculty Details ==========");
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Department: " + department);
        System.out.println("Designation: " + designation);
        System.out.println("Role: " + getRole());
        System.out.println("=====================================");
    }

    // -------------------
    // Getters & Setters
    // -------------------

    // Employee ID: non-null, non-empty, alphanumeric only
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null.");
        }
        String trimmed = employeeId.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty.");
        }
        // Regex Pattern: alphanumeric only
        if (!trimmed.matches("^[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException("Employee ID must contain only alphanumeric characters.");
        }
        this.employeeId = trimmed;
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

    // Designation: letters and spaces only
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        if (designation == null) {
            throw new IllegalArgumentException("Designation cannot be null.");
        }
        String trimmed = designation.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Designation cannot be empty.");
        }
        // Regex Pattern: letters and spaces only
        if (!trimmed.matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("Designation must contain only letters and spaces.");
        }
        this.designation = trimmed;
    }

    // toString (do NOT print password)
    @Override
    public String toString() {
        return "Faculty{" +
                "name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                ", designation='" + designation + '\'' +
                ", role=" + getRole() +
                '}';
        }   

}
