package com.collegeapp.util;

/**
 * Validator - All static input validation methods for the Campus Management
 * System.
 * Used by Service layer before any DAO call.
 * Used by Model setters to enforce field-level validation.
 *
 * All methods return true if valid, false if invalid or null.
 *
 * Developer: Pranav Dadhe
 * Package: com.collegeapp.util
 */
public class Validator {

    // Private constructor - this is a utility class, never instantiate it.
    private Validator() {
    }

    /**
     * Validates email format as local-part@domain.tld.
     *
     * @param email email string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        // Regex: one or more word/symbol chars, '@', domain, '.', 2+ alpha TLD.
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Validates Indian mobile number format.
     *
     * @param phone phone number string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        // Regex: starts with 6-9 followed by exactly 9 digits (total 10).
        return phone.matches("^[6-9]\\d{9}$");
    }

    /**
     * Validates student roll number format.
     *
     * @param rollNo roll number string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidRollNo(String rollNo) {
        if (rollNo == null) {
            return false;
        }
        // Regex: 2 uppercase letters, 2 digits, 2 uppercase letters, 3 digits.
        return rollNo.matches("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{3}$");
    }

    /**
     * Validates faculty employee ID format.
     *
     * @param empId employee ID string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidEmpId(String empId) {
        if (empId == null) {
            return false;
        }
        // Regex: literal EMP followed by exactly 4 digits.
        return empId.matches("^EMP\\d{4}$");
    }

    /**
     * Validates course code format.
     *
     * @param code course code string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidCourseCode(String code) {
        if (code == null) {
            return false;
        }
        // Regex: exactly 2 uppercase letters followed by exactly 3 digits.
        return code.matches("^[A-Z]{2}\\d{3}$");
    }

    /**
     * Validates department code format.
     *
     * @param code department code string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidDeptCode(String code) {
        if (code == null) {
            return false;
        }
        // Regex: uppercase letters only, min 2 and max 5 characters.
        return code.matches("^[A-Z]{2,5}$");
    }

    /**
     * Validates password strength.
     *
     * @param password password string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // Regex: at least one uppercase, at least one digit, minimum length 8.
        return password.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    /**
     * Validates person name with letters and spaces.
     *
     * @param name name string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        // Regex: letters/spaces only, length between 2 and 50.
        return name.matches("^[A-Za-z ]{2,50}$");
    }

    /**
     * Validates username format.
     *
     * @param username username string to validate
     * @return true when valid, false for invalid or null input
     */
    public static boolean isValidUsername(String username) {
        if (username == null) {
            return false;
        }
        // Regex: letters, digits, underscore only, length between 3 and 30.
        return username.matches("^[a-zA-Z0-9_]{3,30}$");
    }
}
