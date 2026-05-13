package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDAO {

    public int insert(User user, int universityId) throws SQLException {
        String sql = """
                INSERT INTO users (university_id, username, email, password_hash, role, is_first_login)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, universityId);
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getRole());
            ps.setBoolean(6, user.isFirstLogin());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating user did not return a generated id.");
    }

    public int insert(User user) throws SQLException {
        return insert(user, 1);
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUserAccount(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean emailExists(String email) throws SQLException {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    public void updatePassword(int userId, String passwordHash, boolean firstLogin) throws SQLException {
        String sql = "UPDATE users SET password_hash = ?, is_first_login = ? WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setBoolean(2, firstLogin);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public boolean delete(int userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public Connection connection() {
        return DBConnection.getConnection();
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static User mapUserAccount(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("created_at");
        return new UserAccount(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("is_first_login"),
                created == null ? null : created.toLocalDateTime());
    }

    private static final class UserAccount extends User {

        private UserAccount(int userId, String username, String email, String passwordHash,
                String role, boolean firstLogin, LocalDateTime createdAt) {
            super(userId, username, email, passwordHash, role, firstLogin, createdAt);
        }

        @Override
        public String getDisplayName() {
            return getUsername();
        }

        @Override
        public String getRole() {
            return getStoredRole();
        }
    }
}
