package com.collegeapp.model;

import java.time.LocalDateTime;

public record UserProfile(
        int profileId,
        int userId,
        String profilePicture,
        String bio,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String pincode,
        String country,
        LocalDateTime updatedAt) {
}
