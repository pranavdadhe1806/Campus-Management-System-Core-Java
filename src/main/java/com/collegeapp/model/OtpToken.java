package com.collegeapp.model;

import java.time.LocalDateTime;

public record OtpToken(
        int otpId,
        int userId,
        String otpCode,
        LocalDateTime otpExpiresAt,
        LocalDateTime createdAt) {
}
