package com.collegeapp.service;

import com.collegeapp.dao.CourseDAO;
import com.collegeapp.model.Course;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() {
        this(new CourseDAO());
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public int addCourse(Course course) throws ServiceException {
        try {
            if (courseDAO.codeExists(course.getCourseCode())) {
                throw new DuplicateEntityException("Course code already exists: " + course.getCourseCode());
            }
            return courseDAO.insert(course);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add course", e);
            throw new ServiceException("Unable to add course.", e);
        }
    }

    public void updateCourse(Course course) throws ServiceException {
        try {
            if (!courseDAO.update(course)) {
                throw new NotFoundException("Course not found: " + course.getCourseId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update course", e);
            throw new ServiceException("Unable to update course.", e);
        }
    }

    public void deleteCourse(int courseId) throws ServiceException {
        try {
            if (!courseDAO.delete(courseId)) {
                throw new NotFoundException("Course not found: " + courseId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete course", e);
            throw new ServiceException("Unable to delete course.", e);
        }
    }

    public Course getCourse(int courseId) throws ServiceException {
        try {
            return courseDAO.findById(courseId)
                    .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch course", e);
            throw new ServiceException("Unable to fetch course.", e);
        }
    }

    public List<Course> listCourses() throws ServiceException {
        try {
            return courseDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list courses", e);
            throw new ServiceException("Unable to list courses.", e);
        }
    }

    public List<Course> listByDepartment(int deptId) throws ServiceException {
        try {
            return courseDAO.findByDept(deptId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list courses by department", e);
            throw new ServiceException("Unable to list courses.", e);
        }
    }
}
