package com.collegeapp.service;

import com.collegeapp.dao.CourseDAO;
import com.collegeapp.dao.EnrollmentDAO;
import com.collegeapp.dao.StudentDAO;
import com.collegeapp.model.Enrollment;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EnrollmentService {

    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    public EnrollmentService() {
        this(new EnrollmentDAO(), new StudentDAO(), new CourseDAO());
    }

    public EnrollmentService(EnrollmentDAO enrollmentDAO, StudentDAO studentDAO, CourseDAO courseDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }

    public int enrollStudent(int studentId, int courseId) throws ServiceException {
        try {
            if (studentDAO.findById(studentId).isEmpty()) {
                throw new NotFoundException("Student not found: " + studentId);
            }
            if (courseDAO.findById(courseId).isEmpty()) {
                throw new NotFoundException("Course not found: " + courseId);
            }
            if (enrollmentDAO.isEnrolled(studentId, courseId)) {
                throw new DuplicateEntityException("Student is already actively enrolled in this course.");
            }
            Enrollment enrollment = new Enrollment(0, studentId, courseId,
                    Enrollment.EnrollmentStatus.ACTIVE, LocalDate.now());
            return enrollmentDAO.enroll(enrollment);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to enroll student", e);
            throw new ServiceException("Unable to enroll student.", e);
        }
    }

    public void dropCourse(int studentId, int courseId) throws ServiceException {
        try {
            if (!enrollmentDAO.drop(studentId, courseId)) {
                throw new NotFoundException("Enrollment not found.");
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to drop course", e);
            throw new ServiceException("Unable to drop course.", e);
        }
    }

    public List<Enrollment> getStudentCourses(int studentId) throws ServiceException {
        try {
            return enrollmentDAO.findByStudent(studentId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list student enrollments", e);
            throw new ServiceException("Unable to list enrollments.", e);
        }
    }

    public List<Enrollment> getCourseStudents(int courseId) throws ServiceException {
        try {
            return enrollmentDAO.findByCourse(courseId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list course enrollments", e);
            throw new ServiceException("Unable to list enrollments.", e);
        }
    }
}
