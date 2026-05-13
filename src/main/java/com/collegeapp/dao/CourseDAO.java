package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAO {

    public int insert(Course course) throws SQLException {
        return insert(course, 1);
    }

    public int insert(Course course, int universityId) throws SQLException {
        String sql = """
                INSERT INTO courses
                (university_id, course_code, course_name, credits, total_marks, lecture_hours,
                 semester, dept_id, faculty_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, universityId);
            bind(ps, course, 2);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    course.setCourseId(keys.getInt(1));
                    return course.getCourseId();
                }
            }
        }
        throw new SQLException("Creating course did not return a generated id.");
    }

    public boolean update(Course course) throws SQLException {
        String sql = """
                UPDATE courses
                SET course_code = ?, course_name = ?, credits = ?, total_marks = ?, lecture_hours = ?,
                    semester = ?, dept_id = ?, faculty_id = ?
                WHERE course_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            bind(ps, course, 1);
            ps.setInt(9, course.getCourseId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int courseId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Course> findById(int courseId) throws SQLException {
        return findOne("SELECT * FROM courses WHERE course_id = ?", courseId);
    }

    public Optional<Course> findByCode(String courseCode) throws SQLException {
        return findOne("SELECT * FROM courses WHERE course_code = ?", courseCode);
    }

    public List<Course> findByDept(int deptId) throws SQLException {
        return findMany("SELECT * FROM courses WHERE dept_id = ? ORDER BY semester, course_code", deptId);
    }

    public List<Course> findAll() throws SQLException {
        return findMany("SELECT * FROM courses ORDER BY semester, course_code");
    }

    public boolean codeExists(String courseCode) throws SQLException {
        return findByCode(courseCode).isPresent();
    }

    private Optional<Course> findOne(String sql, Object value) throws SQLException {
        List<Course> courses = findMany(sql, value);
        return courses.isEmpty() ? Optional.empty() : Optional.of(courses.get(0));
    }

    private List<Course> findMany(String sql, Object... values) throws SQLException {
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(new Course(
                            rs.getInt("course_id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("total_marks"),
                            rs.getInt("lecture_hours"),
                            rs.getInt("semester"),
                            rs.getInt("dept_id"),
                            rs.getInt("faculty_id")));
                }
            }
        }
        return courses;
    }

    private static void bind(PreparedStatement ps, Course course, int offset) throws SQLException {
        ps.setString(offset, course.getCourseCode());
        ps.setString(offset + 1, course.getCourseName());
        ps.setInt(offset + 2, course.getCredits());
        ps.setInt(offset + 3, course.getTotalMarks());
        ps.setInt(offset + 4, course.getLectureHours());
        ps.setInt(offset + 5, course.getSemester());
        ps.setInt(offset + 6, course.getDeptId());
        if (course.getFacultyId() <= 0) {
            ps.setNull(offset + 7, java.sql.Types.INTEGER);
        } else {
            ps.setInt(offset + 7, course.getFacultyId());
        }
    }
}
