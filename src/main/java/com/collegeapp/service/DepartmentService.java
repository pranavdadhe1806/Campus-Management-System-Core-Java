package com.collegeapp.service;

import com.collegeapp.dao.DepartmentDAO;
import com.collegeapp.model.Department;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentService() {
        this(new DepartmentDAO());
    }

    public DepartmentService(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    public int addDepartment(Department department) throws ServiceException {
        try {
            if (departmentDAO.codeExists(department.getDeptCode())) {
                throw new DuplicateEntityException("Department code already exists: " + department.getDeptCode());
            }
            return departmentDAO.insert(department);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add department", e);
            throw new ServiceException("Unable to add department.", e);
        }
    }

    public void updateDepartment(Department department) throws ServiceException {
        try {
            if (!departmentDAO.update(department)) {
                throw new NotFoundException("Department not found: " + department.getDepartmentId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update department", e);
            throw new ServiceException("Unable to update department.", e);
        }
    }

    public void deleteDepartment(int departmentId) throws ServiceException {
        try {
            if (!departmentDAO.delete(departmentId)) {
                throw new NotFoundException("Department not found: " + departmentId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete department", e);
            throw new ServiceException("Unable to delete department. It may still be linked to students, faculty, or courses.", e);
        }
    }

    public List<Department> listAll() throws ServiceException {
        try {
            return departmentDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list departments", e);
            throw new ServiceException("Unable to list departments.", e);
        }
    }
}
