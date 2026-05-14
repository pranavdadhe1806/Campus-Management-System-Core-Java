package com.collegeapp.service;

import com.collegeapp.dao.UniversityDAO;
import com.collegeapp.model.University;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class UniversityService {

    private final UniversityDAO universityDAO = new UniversityDAO();

    public int addUniversity(University university) throws ServiceException {
        try {
            return universityDAO.insert(university);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add university", e);
            throw new ServiceException("Unable to add university.", e);
        }
    }

    public void updateUniversity(University university) throws ServiceException {
        try {
            if (!universityDAO.update(university)) {
                throw new NotFoundException("University not found: " + university.universityId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update university", e);
            throw new ServiceException("Unable to update university.", e);
        }
    }

    public University getUniversity(int universityId) throws ServiceException {
        try {
            return universityDAO.findById(universityId)
                    .orElseThrow(() -> new NotFoundException("University not found: " + universityId));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch university", e);
            throw new ServiceException("Unable to fetch university.", e);
        }
    }

    public List<University> listUniversities() throws ServiceException {
        try {
            return universityDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list universities", e);
            throw new ServiceException("Unable to list universities.", e);
        }
    }
}
