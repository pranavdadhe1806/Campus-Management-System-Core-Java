package com.collegeapp.service;

import com.collegeapp.dao.ExamDAO;
import com.collegeapp.model.Exam;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class ExamService {

    private final ExamDAO examDAO = new ExamDAO();

    public int createExam(Exam exam) throws ServiceException {
        try {
            return examDAO.insert(exam);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to create exam", e);
            throw new ServiceException("Unable to create exam.", e);
        }
    }

    public void updateExam(Exam exam) throws ServiceException {
        try {
            if (!examDAO.update(exam)) {
                throw new NotFoundException("Exam not found: " + exam.examId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update exam", e);
            throw new ServiceException("Unable to update exam.", e);
        }
    }

    public void deleteExam(int examId) throws ServiceException {
        try {
            if (!examDAO.delete(examId)) {
                throw new NotFoundException("Exam not found: " + examId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete exam", e);
            throw new ServiceException("Unable to delete exam.", e);
        }
    }

    public List<Exam> listExams() throws ServiceException {
        try {
            return examDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list exams", e);
            throw new ServiceException("Unable to list exams.", e);
        }
    }
}
