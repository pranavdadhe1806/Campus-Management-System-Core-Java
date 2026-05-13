package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminDAO {

    private final UserDAO userDAO = new UserDAO();

    public int insert(Admin admin) throws SQLException {
        return insert(admin, 1);
    }

    public int insert(Admin admin, int universityId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            int userId = userDAO.insert(admin, universityId);
            String sql = "INSERT INTO admins (first_name, last_name, mobile_no, user_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, admin.getFirstName());
                ps.setString(2, admin.getLastName());
                ps.setString(3, admin.getMobileNo());
                ps.setInt(4, userId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        admin.setUserId(userId);
                        admin.setAdminId(keys.getInt(1));
                    }
                }
            }
            conn.commit();
            return admin.getAdminId();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    public Optional<Admin> findById(int adminId) throws SQLException {
        List<Admin> admins = findMany(baseSelect() + " WHERE a.admin_id = ?", adminId);
        return admins.isEmpty() ? Optional.empty() : Optional.of(admins.get(0));
    }

    public Optional<Admin> findByUserId(int userId) throws SQLException {
        List<Admin> admins = findMany(baseSelect() + " WHERE a.user_id = ?", userId);
        return admins.isEmpty() ? Optional.empty() : Optional.of(admins.get(0));
    }

    public List<Admin> findAll() throws SQLException {
        return findMany(baseSelect() + " ORDER BY a.admin_id");
    }

    private List<Admin> findMany(String sql, Object... values) throws SQLException {
        List<Admin> admins = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp created = rs.getTimestamp("created_at");
                    admins.add(new Admin(
                            rs.getInt("user_id"), rs.getString("username"), rs.getString("email"),
                            rs.getString("password_hash"), rs.getString("role"),
                            rs.getBoolean("is_first_login"),
                            created == null ? null : created.toLocalDateTime(),
                            rs.getInt("admin_id"), rs.getString("first_name"),
                            rs.getString("last_name"), rs.getString("mobile_no")));
                }
            }
        }
        return admins;
    }

    private static String baseSelect() {
        return """
                SELECT u.user_id, u.username, u.email, u.password_hash, u.role, u.is_first_login, u.created_at,
                       a.admin_id, a.first_name, a.last_name, a.mobile_no
                FROM admins a
                JOIN users u ON u.user_id = a.user_id
                """;
    }
}
