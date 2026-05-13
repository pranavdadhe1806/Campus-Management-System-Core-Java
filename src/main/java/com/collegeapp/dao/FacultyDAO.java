package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Faculty;

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

public class FacultyDAO {

    private final UserDAO userDAO = new UserDAO();

    public int insert(Faculty faculty) throws SQLException {
        return insert(faculty, 1);
    }

    public int insert(Faculty faculty, int universityId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            int userId = userDAO.insert(faculty, universityId);
            String sql = """
                    INSERT INTO faculty
                    (employee_id, title, first_name, last_name, designation, mobile_no, dob, user_id, dept_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, faculty.getEmployeeId());
                ps.setString(2, faculty.getTitle());
                ps.setString(3, faculty.getFirstName());
                ps.setString(4, faculty.getLastName());
                ps.setString(5, faculty.getDesignation());
                ps.setString(6, faculty.getMobileNo());
                ps.setDate(7, Date.valueOf(faculty.getDob()));
                ps.setInt(8, userId);
                ps.setInt(9, faculty.getDeptId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        faculty.setUserId(userId);
                        faculty.setFacultyId(keys.getInt(1));
                    }
                }
            }
            conn.commit();
            return faculty.getFacultyId();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    public boolean update(Faculty faculty) throws SQLException {
        String sql = """
                UPDATE faculty
                SET title = ?, first_name = ?, last_name = ?, designation = ?, mobile_no = ?, dob = ?, dept_id = ?
                WHERE faculty_id = ?
                """;
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, faculty.getTitle());
            ps.setString(2, faculty.getFirstName());
            ps.setString(3, faculty.getLastName());
            ps.setString(4, faculty.getDesignation());
            ps.setString(5, faculty.getMobileNo());
            ps.setDate(6, Date.valueOf(faculty.getDob()));
            ps.setInt(7, faculty.getDeptId());
            ps.setInt(8, faculty.getFacultyId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int facultyId) throws SQLException {
        Optional<Faculty> faculty = findById(facultyId);
        return faculty.isPresent() && userDAO.delete(faculty.get().getUserId());
    }

    public Optional<Faculty> findById(int facultyId) throws SQLException {
        return findOne(baseSelect() + " WHERE f.faculty_id = ?", facultyId);
    }

    public Optional<Faculty> findByUserId(int userId) throws SQLException {
        return findOne(baseSelect() + " WHERE f.user_id = ?", userId);
    }

    public Optional<Faculty> findByEmpId(String employeeId) throws SQLException {
        return findOne(baseSelect() + " WHERE f.employee_id = ?", employeeId);
    }

    public List<Faculty> findAll() throws SQLException {
        return findMany(baseSelect() + " ORDER BY f.employee_id");
    }

    public List<Faculty> findByDept(int deptId) throws SQLException {
        return findMany(baseSelect() + " WHERE f.dept_id = ? ORDER BY f.employee_id", deptId);
    }

    public boolean employeeIdExists(String employeeId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("SELECT 1 FROM faculty WHERE employee_id = ?")) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Optional<Faculty> findOne(String sql, Object value) throws SQLException {
        List<Faculty> faculty = findMany(sql, value);
        return faculty.isEmpty() ? Optional.empty() : Optional.of(faculty.get(0));
    }

    private List<Faculty> findMany(String sql, Object... values) throws SQLException {
        List<Faculty> faculty = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    faculty.add(map(rs));
                }
            }
        }
        return faculty;
    }

    private static String baseSelect() {
        return """
                SELECT u.user_id, u.username, u.email, u.password_hash, u.role, u.is_first_login, u.created_at,
                       f.faculty_id, f.employee_id, f.title, f.first_name, f.last_name, f.designation,
                       f.mobile_no, f.dob, f.dept_id
                FROM faculty f
                JOIN users u ON u.user_id = f.user_id
                """;
    }

    private static Faculty map(ResultSet rs) throws SQLException {
        Timestamp created = rs.getTimestamp("created_at");
        return new Faculty(
                rs.getInt("user_id"), rs.getString("username"), rs.getString("email"),
                rs.getString("password_hash"), rs.getString("role"), rs.getBoolean("is_first_login"),
                created == null ? null : created.toLocalDateTime(),
                rs.getInt("faculty_id"), rs.getString("employee_id"), rs.getString("title"),
                rs.getString("first_name"), rs.getString("last_name"), rs.getString("designation"),
                rs.getString("mobile_no"), rs.getDate("dob").toLocalDate(), rs.getInt("dept_id"));
    }
}
