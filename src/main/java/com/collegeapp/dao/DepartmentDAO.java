package com.collegeapp.dao;

import com.collegeapp.DBConnection;
import com.collegeapp.model.Department;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DepartmentDAO {

    public int insert(Department department) throws SQLException {
        return insert(department, 1);
    }

    public int insert(Department department, int universityId) throws SQLException {
        String sql = "INSERT INTO departments (university_id, dept_name, dept_code) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, universityId);
            ps.setString(2, department.getDeptName());
            ps.setString(3, department.getDeptCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    department.setDepartmentId(keys.getInt(1));
                    return department.getDepartmentId();
                }
            }
        }
        throw new SQLException("Creating department did not return a generated id.");
    }

    public boolean update(Department department) throws SQLException {
        String sql = "UPDATE departments SET dept_name = ?, dept_code = ? WHERE department_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, department.getDeptName());
            ps.setString(2, department.getDeptCode());
            ps.setInt(3, department.getDepartmentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int departmentId) throws SQLException {
        try (PreparedStatement ps = DBConnection.getConnection()
                .prepareStatement("DELETE FROM departments WHERE department_id = ?")) {
            ps.setInt(1, departmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<Department> findById(int departmentId) throws SQLException {
        return findOne("SELECT * FROM departments WHERE department_id = ?", departmentId);
    }

    public Optional<Department> findByCode(String deptCode) throws SQLException {
        return findOne("SELECT * FROM departments WHERE dept_code = ?", deptCode);
    }

    public List<Department> findAll() throws SQLException {
        return findMany("SELECT * FROM departments ORDER BY dept_code");
    }

    public boolean codeExists(String deptCode) throws SQLException {
        return findByCode(deptCode).isPresent();
    }

    private Optional<Department> findOne(String sql, Object value) throws SQLException {
        List<Department> departments = findMany(sql, value);
        return departments.isEmpty() ? Optional.empty() : Optional.of(departments.get(0));
    }

    private List<Department> findMany(String sql, Object... values) throws SQLException {
        List<Department> departments = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    departments.add(new Department(
                            rs.getInt("department_id"),
                            rs.getString("dept_name"),
                            rs.getString("dept_code")));
                }
            }
        }
        return departments;
    }
}
