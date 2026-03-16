package com.collegeapp.model;

/**
 * Base abstract user class for Campus Management System.
 * Contains validation in setters so any creation or update goes through checks.
 */
public abstract class User {
    // Attributes of User
    private long id;
    private String name;
    private String email;
    private String password;
    private Role role; // enum: STUDENT, FACULTY, ADMIN

    // Role enum - sealed to prevent unauthorized additions
    public enum Role {
    STUDENT, FACULTY, ADMIN;

        public static Role fromString(String r) {
            return switch (r.toLowerCase()) {
                case "student" -> STUDENT;
                case "faculty" -> FACULTY;
                case "admin" -> ADMIN;
                default -> throw new IllegalArgumentException("Role must be Student, Faculty, or Admin");
            };
        }
    }

    // Default constructor
    protected User() {
    }

    // Parameterized Constructor
    protected User(long id, String name, String email, String password, String role) {
        setId(id);
        setName(name);
        setEmail(email);
        setPassword(password);
        setRole(role);
    }

    // Abstract method to force subclasses to implement their own display logic
    public abstract void displayDetails();

    // -------------------
    // Getters & Setters
    // -------------------

    // ID: must be exactly 10 digits
        public long getId() {
            return id;
        }

        public void setId(long id) {
            String s = String.valueOf(id);
            // Regex Pattern
            if (!s.matches("\\d{10}")) {
                throw new IllegalArgumentException("ID must be exactly 10 digits.");
            }
            this.id = id;
        }

    // Name: only letters and spaces, minimum 2 characters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name cannot be null.");
            }
            String trimmed = name.trim();
            // Regex Pattern
            if (trimmed.length() < 2 || !trimmed.matches("^[A-Za-z ]+$")) {
                throw new IllegalArgumentException("Name must contain only letters and spaces, minimum 2 characters.");
            }
            this.name = trimmed;
        }

    // Email: basic validation (username@domain)
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            if (email == null) {
                throw new IllegalArgumentException("Email cannot be null.");
            }
            String e = email.trim();
            // Enhanced pattern validation using Java 21 switch expressions
            boolean isValid = switch(e.indexOf('@')) {
                case -1 -> false;
                case 0 -> false;
                default -> e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
            };
            if (!isValid) {
                throw new IllegalArgumentException("Invalid email format.");
            }
            this.email = e;
        }

    // Password: min 8, at least 1 upper, 1 lower, 1 digit, 1 special char
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            if (password == null) {
                throw new IllegalArgumentException("Password cannot be null.");
            }
            // Regex Pattern
            String pwdRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!()\\-_*\\[\\]{};:'\",.<>/?\\\\|~`])[A-Za-z\\d@#$%^&+=!()\\-_*\\[\\]{};:'\",.<>/?\\\\|~`]{8,}$";
            if (!password.matches(pwdRegex)) {
                throw new IllegalArgumentException(
                    "Password must be at least 8 characters long and include upper & lower case letters, a digit and a special character."
                );
            }
            this.password = password;
        }

    // Role setters/getter: support both enum and string input
        public String getRole() {
            return (role == null) ? null : role.name();
        }

        public void setRole(Role role) {
            if (role == null) throw new IllegalArgumentException("Role cannot be null.");
            this.role = role;
        }

        // Convenience: accept String role (e.g., from UI dropdown)
        public void setRole(String roleStr) {
            if (roleStr == null) throw new IllegalArgumentException("Role cannot be null.");
            this.role = Role.fromString(roleStr.trim());
        }

    // toString (do NOT print password)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + (role == null ? "null" : role.name()) +
                '}';
    }
}
