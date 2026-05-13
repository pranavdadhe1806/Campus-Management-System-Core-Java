package com.collegeapp.service;

import com.collegeapp.dao.FacultyDAO;
import com.collegeapp.model.Faculty;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class FacultyService {

    private final FacultyDAO facultyDAO;

    public FacultyService() {
        this(new FacultyDAO());
    }

    public FacultyService(FacultyDAO facultyDAO) {
        this.facultyDAO = facultyDAO;
    }

    public int addFaculty(Faculty faculty) throws ServiceException {
        try {
            if (facultyDAO.employeeIdExists(faculty.getEmployeeId())) {
                throw new DuplicateEntityException("Faculty employee ID already exists: " + faculty.getEmployeeId());
            }
            return facultyDAO.insert(faculty);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add faculty", e);
            throw new ServiceException("Unable to add faculty.", e);
        }
    }

    public void updateFaculty(Faculty faculty) throws ServiceException {
        try {
            if (!facultyDAO.update(faculty)) {
                throw new NotFoundException("Faculty not found: " + faculty.getFacultyId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update faculty", e);
            throw new ServiceException("Unable to update faculty.", e);
        }
    }

    public void deleteFaculty(int facultyId) throws ServiceException {
        try {
            if (!facultyDAO.delete(facultyId)) {
                throw new NotFoundException("Faculty not found: " + facultyId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete faculty", e);
            throw new ServiceException("Unable to delete faculty.", e);
        }
    }

    public Faculty getFaculty(int facultyId) throws ServiceException {
        try {
            return facultyDAO.findById(facultyId)
                    .orElseThrow(() -> new NotFoundException("Faculty not found: " + facultyId));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch faculty", e);
            throw new ServiceException("Unable to fetch faculty.", e);
        }
    }

    public List<Faculty> listAll() throws ServiceException {
        try {
            return facultyDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list faculty", e);
            throw new ServiceException("Unable to list faculty.", e);
        }
    }
}
