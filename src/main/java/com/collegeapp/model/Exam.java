package com.collegeapp.model;

import java.time.LocalDateTime;

public record Exam(
        int examId,
        String examName,
        String examType,
        int semester,
        int academicYear,
        int createdBy,
        LocalDateTime createdAt) {
}
