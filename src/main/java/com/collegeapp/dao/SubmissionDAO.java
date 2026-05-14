package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Submission;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubmissionDAO {

    public int insert(Submission submission) throws SQLException {
        String sql = "INSERT INTO submissions (assignment_id, student_id, file_path, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, submission.assignmentId());
            ps.setInt(2, submission.studentId());
            ps.setString(3, submission.filePath());
            ps.setString(4, submission.comment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating submission did not return a generated id.");
    }

    public boolean grade(int submissionId, int marksObtained) throws SQLException {
        String sql = "UPDATE submissions SET marks_obtained = ?, graded_at = CURRENT_TIMESTAMP WHERE submission_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, marksObtained);
            ps.setInt(2, submissionId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Submission> findById(int submissionId) throws SQLException {
        List<Submission> submissions = findMany("SELECT * FROM submissions WHERE submission_id = ?", submissionId);
        return submissions.isEmpty() ? Optional.empty() : Optional.of(submissions.get(0));
    }

    public Optional<Submission> findByAssignmentAndStudent(int assignmentId, int studentId) throws SQLException {
        List<Submission> submissions = findMany(
                "SELECT * FROM submissions WHERE assignment_id = ? AND student_id = ?",
                assignmentId, studentId);
        return submissions.isEmpty() ? Optional.empty() : Optional.of(submissions.get(0));
    }

    public List<Submission> findByAssignment(int assignmentId) throws SQLException {
        return findMany("SELECT * FROM submissions WHERE assignment_id = ? ORDER BY submitted_at DESC", assignmentId);
    }

    public List<Submission> findByStudent(int studentId) throws SQLException {
        return findMany("SELECT * FROM submissions WHERE student_id = ? ORDER BY submitted_at DESC", studentId);
    }

    private List<Submission> findMany(String sql, Object... values) throws SQLException {
        List<Submission> submissions = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    submissions.add(new Submission(
                            rs.getInt("submission_id"),
                            rs.getInt("assignment_id"),
                            rs.getInt("student_id"),
                            rs.getString("file_path"),
                            rs.getString("comment"),
                            DaoSupport.nullableInt(rs, "marks_obtained"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("graded_at")),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("submitted_at"))));
                }
            }
        }
        return submissions;
    }
}
