package com.collegeapp.service;

import com.collegeapp.dao.OtpTokenDAO;
import com.collegeapp.model.OtpToken;
import com.collegeapp.util.LoggerUtil;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OtpService {

    private final OtpTokenDAO otpTokenDAO = new OtpTokenDAO();
    private final SecureRandom random = new SecureRandom();

    public String createOtp(int userId) throws ServiceException {
        String code = String.format("%06d", random.nextInt(1_000_000));
        try {
            otpTokenDAO.deleteForUser(userId);
            otpTokenDAO.insert(new OtpToken(0, userId, code, LocalDateTime.now().plusMinutes(10), null));
            return code;
        } catch (SQLException e) {
            LoggerUtil.error("Failed to create OTP", e);
            throw new ServiceException("Unable to create OTP.", e);
        }
    }

    public boolean verifyOtp(int userId, String code) throws ServiceException {
        try {
            boolean valid = otpTokenDAO.findLatestValid(userId, code).isPresent();
            if (valid) {
                otpTokenDAO.deleteForUser(userId);
            }
            return valid;
        } catch (SQLException e) {
            LoggerUtil.error("Failed to verify OTP", e);
            throw new ServiceException("Unable to verify OTP.", e);
        }
    }

    public int purgeExpired() throws ServiceException {
        try {
            return otpTokenDAO.deleteExpired();
        } catch (SQLException e) {
            LoggerUtil.error("Failed to purge expired OTP tokens", e);
            throw new ServiceException("Unable to purge expired OTP tokens.", e);
        }
    }
}
