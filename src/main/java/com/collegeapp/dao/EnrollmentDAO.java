package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Enrollment;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentDAO {

    public int enroll(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, status, enrolled_on) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getCourseId());
            ps.setString(3, enrollment.getStatus().name());
            if (enrollment.getEnrolledOn() == null) {
                ps.setDate(4, Date.valueOf(java.time.LocalDate.now()));
            } else {
                ps.setDate(4, Date.valueOf(enrollment.getEnrolledOn()));
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    enrollment.setEnrollmentId(keys.getInt(1));
                    return enrollment.getEnrollmentId();
                }
            }
        }
        throw new SQLException("Creating enrollment did not return a generated id.");
    }

    public boolean drop(int studentId, int courseId) throws SQLException {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isEnrolled(int studentId, int courseId) throws SQLException {
        String sql = """
                SELECT 1 FROM enrollments
                WHERE student_id = ? AND course_id = ? AND status = 'ACTIVE'
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<Enrollment> findByStudentAndCourse(int studentId, int courseId) throws SQLException {
        List<Enrollment> enrollments = findMany(
                "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?", studentId, courseId);
        return enrollments.isEmpty() ? Optional.empty() : Optional.of(enrollments.get(0));
    }

    public List<Enrollment> findByStudent(int studentId) throws SQLException {
        return findMany("SELECT * FROM enrollments WHERE student_id = ? ORDER BY enrolled_on DESC", studentId);
    }

    public List<Enrollment> findByCourse(int courseId) throws SQLException {
        return findMany("SELECT * FROM enrollments WHERE course_id = ? ORDER BY enrolled_on DESC", courseId);
    }

    private List<Enrollment> findMany(String sql, Object... values) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date enrolledOn = rs.getDate("enrolled_on");
                    enrollments.add(new Enrollment(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            Enrollment.EnrollmentStatus.valueOf(rs.getString("status")),
                            enrolledOn == null ? null : enrolledOn.toLocalDate()));
                }
            }
        }
        return enrollments;
    }
}
