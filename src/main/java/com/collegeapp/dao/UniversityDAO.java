package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.University;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UniversityDAO {

    public int insert(University university) throws SQLException {
        String sql = "INSERT INTO universities (university_name, allowed_domain, logo_path, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, university.universityName());
            ps.setString(2, university.allowedDomain());
            ps.setString(3, university.logoPath());
            ps.setString(4, university.address());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating university did not return a generated id.");
    }

    public boolean update(University university) throws SQLException {
        String sql = "UPDATE universities SET university_name = ?, allowed_domain = ?, logo_path = ?, address = ? WHERE university_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, university.universityName());
            ps.setString(2, university.allowedDomain());
            ps.setString(3, university.logoPath());
            ps.setString(4, university.address());
            ps.setInt(5, university.universityId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int universityId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM universities WHERE university_id = ?")) {
            ps.setInt(1, universityId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<University> findById(int universityId) throws SQLException {
        return findOne("SELECT * FROM universities WHERE university_id = ?", universityId);
    }

    public Optional<University> findByDomain(String allowedDomain) throws SQLException {
        return findOne("SELECT * FROM universities WHERE allowed_domain = ?", allowedDomain);
    }

    public List<University> findAll() throws SQLException {
        return findMany("SELECT * FROM universities ORDER BY university_name");
    }

    private Optional<University> findOne(String sql, Object value) throws SQLException {
        List<University> items = findMany(sql, value);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    private List<University> findMany(String sql, Object... values) throws SQLException {
        List<University> items = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new University(
                            rs.getInt("university_id"),
                            rs.getString("university_name"),
                            rs.getString("allowed_domain"),
                            rs.getString("logo_path"),
                            rs.getString("address"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return items;
    }
}
