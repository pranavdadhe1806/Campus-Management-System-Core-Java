package com.collegeapp.service;

import com.collegeapp.dao.GradingScaleDAO;
import com.collegeapp.model.GradingScale;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class GradingScaleService {

    private final GradingScaleDAO gradingScaleDAO = new GradingScaleDAO();

    public int addScale(GradingScale scale) throws ServiceException {
        if (scale.minMarks() > scale.maxMarks()) {
            throw new ValidationException("Minimum marks cannot exceed maximum marks.");
        }
        try {
            return gradingScaleDAO.insert(scale);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add grading scale", e);
            throw new ServiceException("Unable to add grading scale.", e);
        }
    }

    public void updateScale(GradingScale scale) throws ServiceException {
        try {
            if (!gradingScaleDAO.update(scale)) {
                throw new NotFoundException("Grade scale not found: " + scale.gradeId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update grading scale", e);
            throw new ServiceException("Unable to update grading scale.", e);
        }
    }

    public List<GradingScale> listByUniversity(int universityId) throws ServiceException {
        try {
            return gradingScaleDAO.findByUniversity(universityId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list grading scale", e);
            throw new ServiceException("Unable to list grading scale.", e);
        }
    }
}
