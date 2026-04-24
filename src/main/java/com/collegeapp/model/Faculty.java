package com.collegeapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.collegeapp.util.Validator;

public class Faculty extends User {

    private int facultyId;
    private String employeeId;
    private String title;
    private String firstName;
    private String lastName;
    private String designation;
    private String mobileNo;
    private LocalDate dob;
    private int deptId;

    public Faculty() {
        super();
    }

    public Faculty(int userId, String username, String email,
            String passwordHash, String role, boolean isFirstLogin,
            LocalDateTime createdAt, int facultyId, String employeeId,
            String title, String firstName, String lastName,
            String designation, String mobileNo, LocalDate dob,
            int deptId) {
        super(userId, username, email, passwordHash, role, isFirstLogin, createdAt);
        setFacultyId(facultyId);
        setEmployeeId(employeeId);
        setTitle(title);
        setFirstName(firstName);
        setLastName(lastName);
        setDesignation(designation);
        setMobileNo(mobileNo);
        setDob(dob);
        setDeptId(deptId);
    }

    @Override
    public String getDisplayName() {
        if (title != null) {
            return title + " " + firstName + " " + lastName;
        }
        return firstName + " " + lastName;
    }

    @Override
    public String getRole() {
        return "FACULTY";
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        if (!Validator.isValidEmpId(employeeId)) {
            throw new IllegalArgumentException(
                    "Invalid employee ID: " + employeeId + ".");
        }
        this.employeeId = employeeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!Validator.isValidName(firstName)) {
            throw new IllegalArgumentException(
                    "Invalid first name: " + firstName + ".");
        }
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!Validator.isValidName(lastName)) {
            throw new IllegalArgumentException(
                    "Invalid last name: " + lastName + ".");
        }
        this.lastName = lastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        if (designation == null || designation.trim().isEmpty()) {
            throw new IllegalArgumentException("Designation cannot be null or empty.");
        }
        this.designation = designation;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        if (!Validator.isValidPhone(mobileNo)) {
            throw new IllegalArgumentException(
                    "Invalid mobile number: " + mobileNo +
                            ". Must be a 10-digit Indian number starting with 6-9.");
        }
        this.mobileNo = mobileNo;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        if (dob == null) {
            throw new IllegalArgumentException("Date of birth cannot be null.");
        }
        this.dob = dob;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", isFirstLogin=" + isFirstLogin() +
                ", createdAt=" + getCreatedAt() +
                ", facultyId=" + facultyId +
                ", employeeId='" + employeeId + '\'' +
                ", title='" + title + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", designation='" + designation + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", dob=" + dob +
                ", deptId=" + deptId +
                '}';
    }
}
