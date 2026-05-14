package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.UserProfile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class UserProfileDAO {

    public int insert(UserProfile profile) throws SQLException {
        String sql = """
                INSERT INTO user_profiles
                (user_id, profile_picture, bio, address_line1, address_line2, city, state, pincode, country)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, profile);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating profile did not return a generated id.");
    }

    public boolean update(UserProfile profile) throws SQLException {
        String sql = """
                UPDATE user_profiles
                SET profile_picture = ?, bio = ?, address_line1 = ?, address_line2 = ?,
                    city = ?, state = ?, pincode = ?, country = ?
                WHERE user_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, profile.profilePicture());
            ps.setString(2, profile.bio());
            ps.setString(3, profile.addressLine1());
            ps.setString(4, profile.addressLine2());
            ps.setString(5, profile.city());
            ps.setString(6, profile.state());
            ps.setString(7, profile.pincode());
            ps.setString(8, profile.country());
            ps.setInt(9, profile.userId());
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<UserProfile> findByUserId(int userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM user_profiles WHERE user_id = ?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public boolean deleteByUserId(int userId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM user_profiles WHERE user_id = ?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private static void bind(PreparedStatement ps, UserProfile profile) throws SQLException {
        ps.setInt(1, profile.userId());
        ps.setString(2, profile.profilePicture());
        ps.setString(3, profile.bio());
        ps.setString(4, profile.addressLine1());
        ps.setString(5, profile.addressLine2());
        ps.setString(6, profile.city());
        ps.setString(7, profile.state());
        ps.setString(8, profile.pincode());
        ps.setString(9, profile.country() == null ? "India" : profile.country());
    }

    private static UserProfile map(ResultSet rs) throws SQLException {
        return new UserProfile(
                rs.getInt("profile_id"),
                rs.getInt("user_id"),
                rs.getString("profile_picture"),
                rs.getString("bio"),
                rs.getString("address_line1"),
                rs.getString("address_line2"),
                rs.getString("city"),
                rs.getString("state"),
                rs.getString("pincode"),
                rs.getString("country"),
                DaoSupport.toLocalDateTime(rs.getTimestamp("updated_at")));
    }
}
