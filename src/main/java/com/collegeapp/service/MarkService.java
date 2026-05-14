package com.collegeapp.service;

import com.collegeapp.dao.MarkDAO;
import com.collegeapp.model.Mark;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class MarkService {

    private final MarkDAO markDAO = new MarkDAO();

    public int addMark(Mark mark) throws ServiceException {
        validateMark(mark);
        try {
            return markDAO.insert(mark);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add mark", e);
            throw new ServiceException("Unable to add mark.", e);
        }
    }

    public void upsertMark(Mark mark) throws ServiceException {
        validateMark(mark);
        try {
            markDAO.upsert(mark);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to upsert mark", e);
            throw new ServiceException("Unable to save mark.", e);
        }
    }

    public List<Mark> listByStudent(int studentId) throws ServiceException {
        try {
            return markDAO.findByStudent(studentId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list marks", e);
            throw new ServiceException("Unable to list marks.", e);
        }
    }

    private static void validateMark(Mark mark) throws ValidationException {
        if (mark.marksObtained() < 0 || mark.totalMarks() <= 0 || mark.marksObtained() > mark.totalMarks()) {
            throw new ValidationException("Marks must be between 0 and total marks.");
        }
    }
}
