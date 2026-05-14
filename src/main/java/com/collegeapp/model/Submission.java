package com.collegeapp.model;

import java.time.LocalDateTime;

public record Submission(
        int submissionId,
        int assignmentId,
        int studentId,
        String filePath,
        String comment,
        Integer marksObtained,
        LocalDateTime gradedAt,
        LocalDateTime submittedAt) {
}
