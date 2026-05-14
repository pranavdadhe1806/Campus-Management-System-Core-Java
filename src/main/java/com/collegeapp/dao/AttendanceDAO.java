package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Attendance;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttendanceDAO {

    public int insert(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendance (student_id, course_id, faculty_id, date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, attendance.studentId());
            ps.setInt(2, attendance.courseId());
            ps.setInt(3, attendance.facultyId());
            ps.setDate(4, Date.valueOf(attendance.date()));
            ps.setString(5, attendance.status().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating attendance did not return a generated id.");
    }

    public boolean updateStatus(int attendanceId, Attendance.AttendanceStatus status) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("UPDATE attendance SET status = ? WHERE attendance_id = ?")) {
            ps.setString(1, status.name());
            ps.setInt(2, attendanceId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Attendance> findByStudentCourseDate(int studentId, int courseId, LocalDate date) throws SQLException {
        return findOne("SELECT * FROM attendance WHERE student_id = ? AND course_id = ? AND date = ?",
                studentId, courseId, Date.valueOf(date));
    }

    public List<Attendance> findByStudent(int studentId) throws SQLException {
        return findMany("SELECT * FROM attendance WHERE student_id = ? ORDER BY date DESC", studentId);
    }

    public List<Attendance> findByCourse(int courseId) throws SQLException {
        return findMany("SELECT * FROM attendance WHERE course_id = ? ORDER BY date DESC", courseId);
    }

    public double attendancePercentage(int studentId, int courseId) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total_classes,
                       SUM(CASE WHEN status IN ('PRESENT', 'LATE') THEN 1 ELSE 0 END) AS attended_classes
                FROM attendance
                WHERE student_id = ? AND course_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt("total_classes") == 0) {
                    return 0.0;
                }
                return (rs.getDouble("attended_classes") / rs.getDouble("total_classes")) * 100.0;
            }
        }
    }

    private Optional<Attendance> findOne(String sql, Object... values) throws SQLException {
        List<Attendance> items = findMany(sql, values);
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    private List<Attendance> findMany(String sql, Object... values) throws SQLException {
        List<Attendance> items = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Attendance(
                            rs.getInt("attendance_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getInt("faculty_id"),
                            rs.getDate("date").toLocalDate(),
                            Attendance.AttendanceStatus.valueOf(rs.getString("status")),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return items;
    }
}
