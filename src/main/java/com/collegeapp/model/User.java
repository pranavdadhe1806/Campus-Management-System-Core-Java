package com.collegeapp.model;

import java.time.LocalDateTime;

import com.collegeapp.util.Validator;

public abstract class User {

    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private boolean isFirstLogin;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(int userId, String username, String email,
            String passwordHash, String role, boolean isFirstLogin,
            LocalDateTime createdAt) {
        setUserId(userId);
        setUsername(username);
        setEmail(email);
        setPasswordHash(passwordHash);
        setRole(role);
        setFirstLogin(isFirstLogin);
        setCreatedAt(createdAt);
    }

    public abstract String getDisplayName();

    public abstract String getRole();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (!Validator.isValidUsername(username)) {
            throw new IllegalArgumentException(
                    "Invalid username: " + username + ".");
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!Validator.isValidEmail(email)) {
            throw new IllegalArgumentException(
                    "Invalid email: " + email + ".");
        }
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.trim().isEmpty()) {
            throw new IllegalArgumentException("passwordHash cannot be null or empty.");
        }
        this.passwordHash = passwordHash;
    }

    protected String getStoredRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", createdAt=" + createdAt +
                '}';
    }
}
