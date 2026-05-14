package com.collegeapp.service;

import com.collegeapp.dao.AssignmentCommentDAO;
import com.collegeapp.dao.AssignmentDAO;
import com.collegeapp.dao.SubmissionDAO;
import com.collegeapp.model.Assignment;
import com.collegeapp.model.AssignmentComment;
import com.collegeapp.model.Submission;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AssignmentService {

    private final AssignmentDAO assignmentDAO = new AssignmentDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final AssignmentCommentDAO assignmentCommentDAO = new AssignmentCommentDAO();

    public int createAssignment(Assignment assignment) throws ServiceException {
        if (assignment.deadline() == null || assignment.deadline().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Assignment deadline must be in the future.");
        }
        if (assignment.totalMarks() <= 0) {
            throw new ValidationException("Assignment total marks must be greater than zero.");
        }
        try {
            return assignmentDAO.insert(assignment);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to create assignment", e);
            throw new ServiceException("Unable to create assignment.", e);
        }
    }

    public void updateAssignment(Assignment assignment) throws ServiceException {
        try {
            if (!assignmentDAO.update(assignment)) {
                throw new NotFoundException("Assignment not found: " + assignment.assignmentId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update assignment", e);
            throw new ServiceException("Unable to update assignment.", e);
        }
    }

    public void deleteAssignment(int assignmentId) throws ServiceException {
        try {
            if (!assignmentDAO.delete(assignmentId)) {
                throw new NotFoundException("Assignment not found: " + assignmentId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete assignment", e);
            throw new ServiceException("Unable to delete assignment.", e);
        }
    }

    public List<Assignment> listByCourse(int courseId) throws ServiceException {
        try {
            return assignmentDAO.findByCourse(courseId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list assignments", e);
            throw new ServiceException("Unable to list assignments.", e);
        }
    }

    public int submit(Submission submission) throws ServiceException {
        try {
            if (submissionDAO.findByAssignmentAndStudent(submission.assignmentId(), submission.studentId()).isPresent()) {
                throw new DuplicateEntityException("Student already submitted this assignment.");
            }
            return submissionDAO.insert(submission);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to submit assignment", e);
            throw new ServiceException("Unable to submit assignment.", e);
        }
    }

    public void gradeSubmission(int submissionId, int marksObtained) throws ServiceException {
        try {
            if (!submissionDAO.grade(submissionId, marksObtained)) {
                throw new NotFoundException("Submission not found: " + submissionId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to grade submission", e);
            throw new ServiceException("Unable to grade submission.", e);
        }
    }

    public List<Submission> listSubmissionsForAssignment(int assignmentId) throws ServiceException {
        try {
            return submissionDAO.findByAssignment(assignmentId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list submissions", e);
            throw new ServiceException("Unable to list submissions.", e);
        }
    }

    public int addComment(AssignmentComment comment) throws ServiceException {
        try {
            return assignmentCommentDAO.insert(comment);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add assignment comment", e);
            throw new ServiceException("Unable to add comment.", e);
        }
    }

    public List<AssignmentComment> listComments(int assignmentId) throws ServiceException {
        try {
            return assignmentCommentDAO.findByAssignment(assignmentId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list assignment comments", e);
            throw new ServiceException("Unable to list comments.", e);
        }
    }
}
