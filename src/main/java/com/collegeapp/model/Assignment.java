package com.collegeapp.model;

import java.time.LocalDateTime;

public record Assignment(
        int assignmentId,
        int courseId,
        int facultyId,
        String title,
        String description,
        String division,
        String batch,
        LocalDateTime deadline,
        int totalMarks,
        LocalDateTime createdAt) {
}
