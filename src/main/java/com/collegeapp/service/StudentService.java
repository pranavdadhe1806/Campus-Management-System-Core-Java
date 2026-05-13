package com.collegeapp.service;

import com.collegeapp.dao.StudentDAO;
import com.collegeapp.model.Student;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this(new StudentDAO());
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public int addStudent(Student student) throws ServiceException {
        try {
            if (studentDAO.rollNumberExists(student.getRollNumber())) {
                throw new DuplicateEntityException("Student roll number already exists: " + student.getRollNumber());
            }
            return studentDAO.insert(student);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to add student", e);
            throw new ServiceException("Unable to add student.", e);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    public void updateStudent(Student student) throws ServiceException {
        try {
            if (!studentDAO.update(student)) {
                throw new NotFoundException("Student not found: " + student.getStudentId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update student", e);
            throw new ServiceException("Unable to update student.", e);
        }
    }

    public void deleteStudent(int studentId) throws ServiceException {
        try {
            if (!studentDAO.delete(studentId)) {
                throw new NotFoundException("Student not found: " + studentId);
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to delete student", e);
            throw new ServiceException("Unable to delete student.", e);
        }
    }

    public Student getStudent(int studentId) throws ServiceException {
        try {
            return studentDAO.findById(studentId)
                    .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch student", e);
            throw new ServiceException("Unable to fetch student.", e);
        }
    }

    public Student getStudentByRollNo(String rollNumber) throws ServiceException {
        try {
            return studentDAO.findByRollNo(rollNumber)
                    .orElseThrow(() -> new NotFoundException("Student not found: " + rollNumber));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch student by roll number", e);
            throw new ServiceException("Unable to fetch student.", e);
        }
    }

    public List<Student> listAll() throws ServiceException {
        try {
            return studentDAO.findAll();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list students", e);
            throw new ServiceException("Unable to list students.", e);
        }
    }

    public List<Student> listByDepartment(int deptId) throws ServiceException {
        try {
            return studentDAO.findByDept(deptId);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to list students by department", e);
            throw new ServiceException("Unable to list students.", e);
        }
    }
}
