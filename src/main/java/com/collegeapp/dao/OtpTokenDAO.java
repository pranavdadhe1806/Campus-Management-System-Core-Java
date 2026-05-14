package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.OtpToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class OtpTokenDAO {

    public int insert(OtpToken token) throws SQLException {
        String sql = "INSERT INTO otp_tokens (user_id, otp_code, otp_expires_at) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, token.userId());
            ps.setString(2, token.otpCode());
            ps.setTimestamp(3, DaoSupport.toTimestamp(token.otpExpiresAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating OTP token did not return a generated id.");
    }

    public Optional<OtpToken> findLatestValid(int userId, String otpCode) throws SQLException {
        String sql = """
                SELECT * FROM otp_tokens
                WHERE user_id = ? AND otp_code = ? AND otp_expires_at > CURRENT_TIMESTAMP
                ORDER BY created_at DESC
                LIMIT 1
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, otpCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public int deleteForUser(int userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM otp_tokens WHERE user_id = ?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }

    public int deleteExpired() throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM otp_tokens WHERE otp_expires_at <= CURRENT_TIMESTAMP")) {
            return ps.executeUpdate();
        }
    }

    private static OtpToken map(ResultSet rs) throws SQLException {
        return new OtpToken(
                rs.getInt("otp_id"),
                rs.getInt("user_id"),
                rs.getString("otp_code"),
                DaoSupport.toLocalDateTime(rs.getTimestamp("otp_expires_at")),
                DaoSupport.toLocalDateTime(rs.getTimestamp("created_at")));
    }
}
