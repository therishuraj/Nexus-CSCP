package com.nexus.user_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
    
    /**
     * Get logger instance for a class
     * @param clazz the class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Log info message with formatted parameters
     * @param logger the logger instance
     * @param message the message template
     * @param params the parameters
     */
    public static void logInfo(Logger logger, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(message, params);
        }
    }
    
    /**
     * Log error message with exception
     * @param logger the logger instance
     * @param message the message
     * @param throwable the exception
     */
    public static void logError(Logger logger, String message, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(message, throwable);
        }
    }
    
    /**
     * Log warning message
     * @param logger the logger instance
     * @param message the message template
     * @param params the parameters
     */
    public static void logWarn(Logger logger, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, params);
        }
    }
    
    /**
     * Log debug message
     * @param logger the logger instance
     * @param message the message template
     * @param params the parameters
     */
    public static void logDebug(Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(message, params);
        }
    }
}
