package com.collegeapp.model;

/**
 * Department entity representing a department in the Campus Management System.
 * Standalone domain entity (does not extend User).
 * Follows validation-in-setters pattern.
 */
public class Department {
    // Department attributes
    private long departmentId;      // System/internal ID
    private String departmentCode;  // User-visible identifier (e.g., CSE, ECE)
    private String departmentName;

    // -------------------
    // Constructors
    // -------------------

    // Default Constructor
    public Department() {
    }

    // Parameterized Constructor
    public Department(long departmentId, String departmentCode, String departmentName) {
        this.departmentId = departmentId;
        setDepartmentCode(departmentCode);
        setDepartmentName(departmentName);
    }

    // -------------------
    // Getters & Setters
    // -------------------

    // Department ID: system/internal identifier (immutable)
    public long getDepartmentId() {
        return departmentId;
    }

    // Department Code: non-null, non-empty, uppercase letters only, min length 2
    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        if (departmentCode == null) {
            throw new IllegalArgumentException("Department code cannot be null.");
        }
        String trimmed = departmentCode.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Department code cannot be empty.");
        }
        if (trimmed.length() < 2) {
            throw new IllegalArgumentException("Department code must be at least 2 characters long.");
        }
        // Regex Pattern: uppercase letters only
        if (!trimmed.matches("^[A-Z]+$")) {
            throw new IllegalArgumentException("Department code must contain only uppercase letters.");
        }
        this.departmentCode = trimmed;
    }

    // Department Name: letters and spaces only
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        if (departmentName == null) {
            throw new IllegalArgumentException("Department name cannot be null.");
        }
        String trimmed = departmentName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be empty.");
        }
        // Regex Pattern: letters and spaces only
        if (!trimmed.matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("Department name must contain only letters and spaces.");
        }
        this.departmentName = trimmed;
    }

    // toString (do NOT include system departmentId)
    @Override
    public String toString() {
        return "Department{" +
                "departmentCode='" + departmentCode + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}