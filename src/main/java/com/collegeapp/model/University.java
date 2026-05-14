package com.collegeapp.model;

import java.time.LocalDateTime;

public record University(
        int universityId,
        String universityName,
        String allowedDomain,
        String logoPath,
        String address,
        LocalDateTime createdAt) {
}
