package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Student;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    private final UserDAO userDAO = new UserDAO();

    public int insert(Student student) throws SQLException {
        return insert(student, 1);
    }

    public int insert(Student student, int universityId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            int userId = userDAO.insert(student, universityId);
            String sql = """
                    INSERT INTO students
                    (roll_number, first_name, last_name, mobile_no, dob, academic_year,
                     division, batch, sem, user_id, dept_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bindProfile(ps, student);
                ps.setInt(10, userId);
                ps.setInt(11, student.getDeptId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        student.setUserId(userId);
                        student.setStudentId(keys.getInt(1));
                    }
                }
            }
            conn.commit();
            return student.getStudentId();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    public boolean update(Student student) throws SQLException {
        String sql = """
                UPDATE students
                SET first_name = ?, last_name = ?, mobile_no = ?, dob = ?, academic_year = ?,
                    division = ?, batch = ?, sem = ?, dept_id = ?
                WHERE student_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setString(3, student.getMobileNo());
            ps.setDate(4, Date.valueOf(student.getDob()));
            ps.setInt(5, student.getAcademicYear());
            ps.setString(6, student.getDivision());
            ps.setString(7, student.getBatch());
            ps.setInt(8, student.getSem());
            ps.setInt(9, student.getDeptId());
            ps.setInt(10, student.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int studentId) throws SQLException {
        Optional<Student> student = findById(studentId);
        return student.isPresent() && userDAO.delete(student.get().getUserId());
    }

    public Optional<Student> findById(int studentId) throws SQLException {
        String sql = baseSelect() + " WHERE s.student_id = ?";
        return findOne(sql, studentId);
    }

    public Optional<Student> findByUserId(int userId) throws SQLException {
        String sql = baseSelect() + " WHERE s.user_id = ?";
        return findOne(sql, userId);
    }

    public Optional<Student> findByRollNo(String rollNumber) throws SQLException {
        String sql = baseSelect() + " WHERE s.roll_number = ?";
        return findOne(sql, rollNumber);
    }

    public List<Student> findAll() throws SQLException {
        String sql = baseSelect() + " ORDER BY s.roll_number";
        return findMany(sql);
    }

    public List<Student> findByDept(int deptId) throws SQLException {
        String sql = baseSelect() + " WHERE s.dept_id = ? ORDER BY s.roll_number";
        return findMany(sql, deptId);
    }

    public boolean rollNumberExists(String rollNumber) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT 1 FROM students WHERE roll_number = ?")) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Optional<Student> findOne(String sql, Object value) throws SQLException {
        List<Student> students = findMany(sql, value);
        return students.isEmpty() ? Optional.empty() : Optional.of(students.get(0));
    }

    private List<Student> findMany(String sql, Object... values) throws SQLException {
        List<Student> students = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(map(rs));
                }
            }
        }
        return students;
    }

    private static String baseSelect() {
        return """
                SELECT u.user_id, u.username, u.email, u.password_hash, u.role, u.is_first_login, u.created_at,
                       s.student_id, s.roll_number, s.first_name, s.last_name, s.mobile_no, s.dob,
                       s.academic_year, s.division, s.batch, s.sem, s.dept_id
                FROM students s
                JOIN users u ON u.user_id = s.user_id
                """;
    }

    private static void bindProfile(PreparedStatement ps, Student student) throws SQLException {
        ps.setString(1, student.getRollNumber());
        ps.setString(2, student.getFirstName());
        ps.setString(3, student.getLastName());
        ps.setString(4, student.getMobileNo());
        ps.setDate(5, Date.valueOf(student.getDob()));
        ps.setInt(6, student.getAcademicYear());
        ps.setString(7, student.getDivision());
        ps.setString(8, student.getBatch());
        ps.setInt(9, student.getSem());
    }

    private static Student map(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("created_at");
        return new Student(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("role"),
                rs.getBoolean("is_first_login"),
                created == null ? null : created.toLocalDateTime(),
                rs.getInt("student_id"),
                rs.getString("roll_number"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("mobile_no"),
                rs.getDate("dob").toLocalDate(),
                rs.getInt("academic_year"),
                rs.getString("division"),
                rs.getString("batch"),
                rs.getInt("sem"),
                rs.getInt("dept_id"));
    }
}
