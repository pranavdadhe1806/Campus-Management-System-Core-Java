package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Exam;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {

    public int insert(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (exam_name, exam_type, semester, academic_year, created_by) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, exam.examName());
            ps.setString(2, exam.examType());
            ps.setInt(3, exam.semester());
            ps.setInt(4, exam.academicYear());
            ps.setInt(5, exam.createdBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating exam did not return a generated id.");
    }

    public boolean update(Exam exam) throws SQLException {
        String sql = "UPDATE exams SET exam_name = ?, exam_type = ?, semester = ?, academic_year = ? WHERE exam_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, exam.examName());
            ps.setString(2, exam.examType());
            ps.setInt(3, exam.semester());
            ps.setInt(4, exam.academicYear());
            ps.setInt(5, exam.examId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int examId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM exams WHERE exam_id = ?")) {
            ps.setInt(1, examId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Exam> findById(int examId) throws SQLException {
        List<Exam> exams = findMany("SELECT * FROM exams WHERE exam_id = ?", examId);
        return exams.isEmpty() ? Optional.empty() : Optional.of(exams.get(0));
    }

    public List<Exam> findBySemester(int semester, int academicYear) throws SQLException {
        return findMany("SELECT * FROM exams WHERE semester = ? AND academic_year = ? ORDER BY created_at DESC",
                semester, academicYear);
    }

    public List<Exam> findAll() throws SQLException {
        return findMany("SELECT * FROM exams ORDER BY created_at DESC");
    }

    private List<Exam> findMany(String sql, Object... values) throws SQLException {
        List<Exam> exams = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    exams.add(new Exam(
                            rs.getInt("exam_id"),
                            rs.getString("exam_name"),
                            rs.getString("exam_type"),
                            rs.getInt("semester"),
                            rs.getInt("academic_year"),
                            rs.getInt("created_by"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return exams;
    }
}
