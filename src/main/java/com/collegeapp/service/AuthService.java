package com.collegeapp.service;

import com.collegeapp.dao.AdminDAO;
import com.collegeapp.dao.FacultyDAO;
import com.collegeapp.dao.StudentDAO;
import com.collegeapp.dao.UserDAO;
import com.collegeapp.model.User;
import com.collegeapp.util.LoggerUtil;
import com.collegeapp.util.PasswordUtil;
import com.collegeapp.util.Validator;

import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final FacultyDAO facultyDAO;
    private final AdminDAO adminDAO;

    public AuthService() {
        this(new UserDAO(), new StudentDAO(), new FacultyDAO(), new AdminDAO());
    }

    public AuthService(UserDAO userDAO, StudentDAO studentDAO, FacultyDAO facultyDAO, AdminDAO adminDAO) {
        this.userDAO = userDAO;
        this.studentDAO = studentDAO;
        this.facultyDAO = facultyDAO;
        this.adminDAO = adminDAO;
    }

    public User login(String email, String password) throws ServiceException {
        if (!Validator.isValidEmail(email)) {
            throw new ValidationException("Invalid email format.");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationException("Password cannot be empty.");
        }

        try {
            User account = userDAO.findByEmail(email)
                    .orElseThrow(() -> new AuthenticationException("User not found."));
            String suppliedHash = PasswordUtil.sha256(password);
            if (!suppliedHash.equalsIgnoreCase(account.getPasswordHash())) {
                throw new AuthenticationException("Incorrect password.");
            }
            return loadTypedUser(account);
        } catch (SQLException e) {
            LoggerUtil.error("Login failed", e);
            throw new ServiceException("Unable to login.", e);
        }
    }

    public void changePassword(int userId, String newPassword) throws ServiceException {
        if (!Validator.isValidPassword(newPassword)) {
            throw new ValidationException("Password must be at least 8 characters and include an uppercase letter and digit.");
        }
        try {
            userDAO.updatePassword(userId, PasswordUtil.sha256(newPassword), false);
        } catch (SQLException e) {
            LoggerUtil.error("Password update failed", e);
            throw new ServiceException("Unable to update password.", e);
        }
    }

    private User loadTypedUser(User account) throws SQLException {
        return switch (account.getRole()) {
            case "STUDENT" -> studentDAO.findByUserId(account.getUserId()).map(User.class::cast).orElse(account);
            case "FACULTY" -> facultyDAO.findByUserId(account.getUserId()).map(User.class::cast).orElse(account);
            case "ADMIN", "SUPER_ADMIN" -> adminDAO.findByUserId(account.getUserId()).map(User.class::cast).orElse(account);
            default -> account;
        };
    }
}
