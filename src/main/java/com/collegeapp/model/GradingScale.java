package com.collegeapp.model;

import java.math.BigDecimal;

public record GradingScale(
        int gradeId,
        int universityId,
        int minMarks,
        int maxMarks,
        String grade,
        BigDecimal gradePoints) {
}
