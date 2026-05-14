package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.GradingScale;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradingScaleDAO {

    public int insert(GradingScale scale) throws SQLException {
        String sql = "INSERT INTO grading_scale (university_id, min_marks, max_marks, grade, grade_points) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, scale.universityId());
            ps.setInt(2, scale.minMarks());
            ps.setInt(3, scale.maxMarks());
            ps.setString(4, scale.grade());
            ps.setBigDecimal(5, scale.gradePoints());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating grading scale did not return a generated id.");
    }

    public boolean update(GradingScale scale) throws SQLException {
        String sql = "UPDATE grading_scale SET min_marks = ?, max_marks = ?, grade = ?, grade_points = ? WHERE grade_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, scale.minMarks());
            ps.setInt(2, scale.maxMarks());
            ps.setString(3, scale.grade());
            ps.setBigDecimal(4, scale.gradePoints());
            ps.setInt(5, scale.gradeId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int gradeId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM grading_scale WHERE grade_id = ?")) {
            ps.setInt(1, gradeId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<GradingScale> findByUniversity(int universityId) throws SQLException {
        return findMany("SELECT * FROM grading_scale WHERE university_id = ? ORDER BY min_marks", universityId);
    }

    public Optional<GradingScale> findForMarks(int universityId, int marks) throws SQLException {
        List<GradingScale> scales = findMany(
                "SELECT * FROM grading_scale WHERE university_id = ? AND ? BETWEEN min_marks AND max_marks LIMIT 1",
                universityId, marks);
        return scales.isEmpty() ? Optional.empty() : Optional.of(scales.get(0));
    }

    private List<GradingScale> findMany(String sql, Object... values) throws SQLException {
        List<GradingScale> scales = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    scales.add(new GradingScale(
                            rs.getInt("grade_id"),
                            rs.getInt("university_id"),
                            rs.getInt("min_marks"),
                            rs.getInt("max_marks"),
                            rs.getString("grade"),
                            rs.getBigDecimal("grade_points")));
                }
            }
        }
        return scales;
    }
}
