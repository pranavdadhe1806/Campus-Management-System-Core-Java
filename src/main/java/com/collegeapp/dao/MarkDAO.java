package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Mark;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MarkDAO {

    public int insert(Mark mark) throws SQLException {
        String sql = """
                INSERT INTO marks (exam_id, student_id, course_id, faculty_id, marks_obtained, total_marks)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, mark);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating mark did not return a generated id.");
    }

    public boolean update(Mark mark) throws SQLException {
        String sql = "UPDATE marks SET faculty_id = ?, marks_obtained = ?, total_marks = ? WHERE mark_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, mark.facultyId());
            ps.setInt(2, mark.marksObtained());
            ps.setInt(3, mark.totalMarks());
            ps.setInt(4, mark.markId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean upsert(Mark mark) throws SQLException {
        String sql = """
                INSERT INTO marks (exam_id, student_id, course_id, faculty_id, marks_obtained, total_marks)
                VALUES (?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    faculty_id = VALUES(faculty_id),
                    marks_obtained = VALUES(marks_obtained),
                    total_marks = VALUES(total_marks)
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            bind(ps, mark);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Mark> findById(int markId) throws SQLException {
        List<Mark> marks = findMany("SELECT * FROM marks WHERE mark_id = ?", markId);
        return marks.isEmpty() ? Optional.empty() : Optional.of(marks.get(0));
    }

    public Optional<Mark> findByExamStudentCourse(int examId, int studentId, int courseId) throws SQLException {
        List<Mark> marks = findMany(
                "SELECT * FROM marks WHERE exam_id = ? AND student_id = ? AND course_id = ?",
                examId, studentId, courseId);
        return marks.isEmpty() ? Optional.empty() : Optional.of(marks.get(0));
    }

    public List<Mark> findByStudent(int studentId) throws SQLException {
        return findMany("SELECT * FROM marks WHERE student_id = ? ORDER BY created_at DESC", studentId);
    }

    public List<Mark> findByCourse(int courseId) throws SQLException {
        return findMany("SELECT * FROM marks WHERE course_id = ? ORDER BY created_at DESC", courseId);
    }

    private List<Mark> findMany(String sql, Object... values) throws SQLException {
        List<Mark> marks = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    marks.add(new Mark(
                            rs.getInt("mark_id"),
                            rs.getInt("exam_id"),
                            rs.getInt("student_id"),
                            rs.getInt("course_id"),
                            rs.getInt("faculty_id"),
                            rs.getInt("marks_obtained"),
                            rs.getInt("total_marks"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return marks;
    }

    private static void bind(PreparedStatement ps, Mark mark) throws SQLException {
        ps.setInt(1, mark.examId());
        ps.setInt(2, mark.studentId());
        ps.setInt(3, mark.courseId());
        ps.setInt(4, mark.facultyId());
        ps.setInt(5, mark.marksObtained());
        ps.setInt(6, mark.totalMarks());
    }
}
