package com.collegeapp.model;

import java.time.LocalDate;

public class Enrollment {

    public enum EnrollmentStatus {
        ACTIVE,
        DROPPED,
        COMPLETED
    }

    private int enrollmentId;
    private int studentId;
    private int courseId;
    private EnrollmentStatus status;
    private LocalDate enrolledOn;

    public Enrollment() {
    }

    public Enrollment(int enrollmentId, int studentId, int courseId,
            EnrollmentStatus status, LocalDate enrolledOn) {
        setEnrollmentId(enrollmentId);
        setStudentId(studentId);
        setCourseId(courseId);
        setStatus(status);
        setEnrolledOn(enrolledOn);
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Enrollment status cannot be null.");
        }
        this.status = status;
    }

    public LocalDate getEnrolledOn() {
        return enrolledOn;
    }

    public void setEnrolledOn(LocalDate enrolledOn) {
        this.enrolledOn = enrolledOn;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", status=" + status +
                ", enrolledOn=" + enrolledOn +
                '}';
    }
}
