package com.collegeapp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * LoggerUtil - Centralized logging utility for the Campus Management System.
 * Wraps java.util.logging.Logger.
 * Writes all logs to logs/app.log.
 *
 * Usage:
 * LoggerUtil.info("Student added");
 * LoggerUtil.warn("Duplicate attempt");
 * LoggerUtil.error("DB failed", exception);
 *
 * Developer: Pranav Dadhe
 * Package: com.collegeapp.util
 */
public class LoggerUtil {

    // Single shared logger instance for the entire app.
    private static final Logger logger = Logger.getLogger("com.collegeapp");

    // Static block runs once when class is first loaded.
    static {
        try {
            Files.createDirectories(Paths.get("logs"));
            FileHandler fileHandler = new FileHandler("logs/app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException | SecurityException ex) {
            // Fallback to default console logging and do not fail application startup.
            logger.log(Level.WARNING, "Failed to initialize file logger. Falling back to console logging.", ex);
        }
    }

    // Private constructor - this is a utility class, never instantiate it.
    private LoggerUtil() {
    }

    /**
     * Logs a successful or informational application event.
     *
     * @param message message to log at INFO level
     */
    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    /**
     * Logs a non-critical warning event.
     *
     * @param message message to log at WARNING level
     */
    public static void warn(String message) {
        logger.log(Level.WARNING, message);
    }

    /**
     * Logs a critical error event with exception details.
     *
     * @param message   message to log at SEVERE level
     * @param throwable exception to include in the log
     */
    public static void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }
}
