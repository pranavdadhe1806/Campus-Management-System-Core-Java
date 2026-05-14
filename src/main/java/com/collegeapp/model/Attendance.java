package com.collegeapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Attendance(
        int attendanceId,
        int studentId,
        int courseId,
        int facultyId,
        LocalDate date,
        AttendanceStatus status,
        LocalDateTime createdAt) {

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE
    }
}
