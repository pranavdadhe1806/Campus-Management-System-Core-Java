package com.collegeapp.model;

import java.time.LocalDateTime;

import com.collegeapp.util.Validator;

public class Admin extends User {

    private int adminId;
    private String firstName;
    private String lastName;
    private String mobileNo;

    public Admin() {
        super();
    }

    public Admin(int userId, String username, String email,
            String passwordHash, String role, boolean isFirstLogin,
            LocalDateTime createdAt, int adminId, String firstName,
            String lastName, String mobileNo) {
        super(userId, username, email, passwordHash, role, isFirstLogin, createdAt);
        setAdminId(adminId);
        setFirstName(firstName);
        setLastName(lastName);
        setMobileNo(mobileNo);
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
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

    @Override
    public String toString() {
        return "Admin{" +
                "userId=" + getUserId() +
                ", username='" + getUsername() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", isFirstLogin=" + isFirstLogin() +
                ", createdAt=" + getCreatedAt() +
                ", adminId=" + adminId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                '}';
    }
}
