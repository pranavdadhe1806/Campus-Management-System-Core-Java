package com.collegeapp.model;

import java.time.LocalDateTime;

public record AssignmentComment(
        int commentId,
        int assignmentId,
        int userId,
        String comment,
        LocalDateTime createdAt) {
}
