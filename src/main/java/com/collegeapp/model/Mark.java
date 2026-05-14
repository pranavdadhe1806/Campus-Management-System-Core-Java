package com.collegeapp.model;

import java.time.LocalDateTime;

public record Mark(
        int markId,
        int examId,
        int studentId,
        int courseId,
        int facultyId,
        int marksObtained,
        int totalMarks,
        LocalDateTime createdAt) {
}
