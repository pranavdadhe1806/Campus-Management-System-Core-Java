package com.collegeapp.service;

import com.collegeapp.dao.AttendanceDAO;
import com.collegeapp.model.Attendance;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceService {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public int markAttendance(Attendance attendance) throws ServiceException {
        try {
            if (attendanceDAO.findByStudentCourseDate(attendance.studentId(), attendance.courseId(), attendance.date()).isPresent()) {
                throw new DuplicateEntityException("Attendance already marked for this student, course, and date.");
            }
            return attendanceDAO.insert(attendance);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to mark attendance", e);
            throw new ServiceException("Unable to mark attendance.", e);
        }
    }

    public void updateStatus(int attendanceId, Attendance.AttendanceStatus status) throws ServiceException {
        try {
            if (!attendanceDAO.updateStatus(attendanceId, status)) {
                throw new NotFoundException("Attendance not found: " + attendanceId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update attendance", e);
            throw new ServiceException("Unable to update attendance.", e);
        }
    }

    public List<Attendance> listByStudent(int studentId) throws ServiceException {
        try {
            return attendanceDAO.findByStudent(studentId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list student attendance", e);
            throw new ServiceException("Unable to list attendance.", e);
        }
    }

    public double attendancePercentage(int studentId, int courseId) throws ServiceException {
        try {
            return attendanceDAO.attendancePercentage(studentId, courseId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to calculate attendance", e);
            throw new ServiceException("Unable to calculate attendance.", e);
        }
    }
}
