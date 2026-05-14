package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.AssignmentComment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AssignmentCommentDAO {

    public int insert(AssignmentComment comment) throws SQLException {
        String sql = "INSERT INTO assignment_comments (assignment_id, user_id, comment) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.assignmentId());
            ps.setInt(2, comment.userId());
            ps.setString(3, comment.comment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Creating assignment comment did not return a generated id.");
    }

    public boolean delete(int commentId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM assignment_comments WHERE comment_id = ?")) {
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<AssignmentComment> findByAssignment(int assignmentId) throws SQLException {
        List<AssignmentComment> comments = new ArrayList<>();
        String sql = "SELECT * FROM assignment_comments WHERE assignment_id = ? ORDER BY created_at";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(new AssignmentComment(
                            rs.getInt("comment_id"),
                            rs.getInt("assignment_id"),
                            rs.getInt("user_id"),
                            rs.getString("comment"),
                            DaoSupport.toLocalDateTime(rs.getTimestamp("created_at"))));
                }
            }
        }
        return comments;
    }
}
