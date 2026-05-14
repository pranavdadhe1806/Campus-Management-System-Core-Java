package com.collegeapp.service;

import com.collegeapp.dao.UserProfileDAO;
import com.collegeapp.model.UserProfile;
import com.collegeapp.util.LoggerUtil;

import java.sql.SQLException;

public class UserProfileService {

    private final UserProfileDAO userProfileDAO = new UserProfileDAO();

    public int createProfile(UserProfile profile) throws ServiceException {
        try {
            return userProfileDAO.insert(profile);
        } catch (SQLException e) {
            LoggerUtil.error("Failed to create user profile", e);
            throw new ServiceException("Unable to create profile.", e);
        }
    }

    public void updateProfile(UserProfile profile) throws ServiceException {
        try {
            if (!userProfileDAO.update(profile)) {
                throw new NotFoundException("Profile not found for user: " + profile.userId());
            }
        } catch (SQLException e) {
            LoggerUtil.error("Failed to update user profile", e);
            throw new ServiceException("Unable to update profile.", e);
        }
    }

    public UserProfile getByUserId(int userId) throws ServiceException {
        try {
            return userProfileDAO.findByUserId(userId)
                    .orElseThrow(() -> new NotFoundException("Profile not found for user: " + userId));
        } catch (SQLException e) {
            LoggerUtil.error("Failed to fetch user profile", e);
            throw new ServiceException("Unable to fetch profile.", e);
        }
    }
}
