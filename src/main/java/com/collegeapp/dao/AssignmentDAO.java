package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Assignment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssignmentDAO {

    public int insert(Assignment assignment) throws SQLException {
        String sql = """
                INSERT INTO assignments
                (course_id, faculty_id, title, description, division, batch, deadline, total_marks)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, assignment);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating assignment did not return a generated id.");
    }

    public boolean update(Assignment assignment) throws SQLException {
        String sql = """
                UPDATE assignments
                SET title = ?, description = ?, division = ?, batch = ?, deadline = ?, total_marks = ?
                WHERE assignment_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, assignment.title());
            ps.setString(2, assignment.description());
            ps.setString(3, assignment.division());
            ps.setString(4, assignment.batch());
            ps.setTimestamp(5, DaoSupport.toTimestamp(assignment.deadline()));
            ps.setInt(6, assignment.totalMarks());
            ps.setInt(7, assignment.assignmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int assignmentId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM assignments WHERE assignment_id = ?")) {
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Assignment> findById(int assignmentId) throws SQLException {
        List<Assignment> assignments = findMany("SELECT * FROM assignments WHERE assignment_id = ?", assignmentId);
        return assignments.isEmpty() ? Optional.empty() : Optional.of(assignments.get(0));
    }

    public List<Assignment> findByCourse(int courseId) throws SQLException {
        return findMany("SELECT * FROM assignments WHERE course_id = ? ORDER BY deadline DESC", courseId);
    }

    public List<Assignment> findByFaculty(int facultyId) throws SQLException {
        return findMany("SELECT * FROM assignments WHERE faculty_id = ? ORDER BY deadline DESC", facultyId);
    }

    public List<Assignment> findForStudent(int courseId, String division, String batch) throws SQLException {
        String sql = """
                SELECT * FROM assignments
                WHERE course_id = ?
                  AND (division IS NULL OR division = ?)
                  AND (batch IS NULL OR batch = ?)
                ORDER BY deadline DESC
                """;
        return findMany(sql, courseId, division, batch);
    }

    private List<Assignment> findMany(String sql, Object... values) throws SQLException {
        List<Assignment> assignments = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    assignments.add(new Assignment(
                            rs.getInt("assignment_id"),
                            rs.getInt("course_id"),
                            rs.getInt("faculty_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("division"),
                            rs.getString("batch"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("deadline")),
                            rs.getInt("total_marks"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return assignments;
    }

    private static void bind(PreparedStatement ps, Assignment assignment) throws SQLException {
        ps.setInt(1, assignment.courseId());
        ps.setInt(2, assignment.facultyId());
        ps.setString(3, assignment.title());
        ps.setString(4, assignment.description());
        ps.setString(5, assignment.division());
        ps.setString(6, assignment.batch());
        ps.setTimestamp(7, DaoSupport.toTimestamp(assignment.deadline()));
        ps.setInt(8, assignment.totalMarks());
    }
}
