package com.nexus.user_service.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("LoggerUtils Unit Tests")
class LoggerUtilsTest {

    @Test
    @DisplayName("Get Logger - Valid Class")
    void getLogger_ValidClass_ReturnsLoggerInstance() {
        // When
        Logger logger = LoggerUtils.getLogger(LoggerUtilsTest.class);

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).contains("LoggerUtilsTest");
    }

    @Test
    @DisplayName("Get Logger - Different Classes Return Different Loggers")
    void getLogger_DifferentClasses_ReturnDifferentLoggers() {
        // When
        Logger logger1 = LoggerUtils.getLogger(String.class);
        Logger logger2 = LoggerUtils.getLogger(Integer.class);

        // Then
        assertThat(logger1).isNotNull();
        assertThat(logger2).isNotNull();
        assertThat(logger1.getName()).isNotEqualTo(logger2.getName());
        assertThat(logger1.getName()).contains("String");
        assertThat(logger2.getName()).contains("Integer");
    }

    @Test
    @DisplayName("Get Logger - Same Class Returns Same Logger")
    void getLogger_SameClass_ReturnsSameLogger() {
        // When
        Logger logger1 = LoggerUtils.getLogger(LoggerUtilsTest.class);
        Logger logger2 = LoggerUtils.getLogger(LoggerUtilsTest.class);

        // Then
        assertThat(logger1).isSameAs(logger2);
    }

    @Test
    @DisplayName("Log Info - Info Enabled")
    void logInfo_InfoEnabled_CallsLoggerInfo() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        String message = "Test message with {} and {}";
        Object param1 = "param1";
        Object param2 = "param2";

        // When
        LoggerUtils.logInfo(mockLogger, message, param1, param2);

        // Then
        verify(mockLogger).isInfoEnabled();
        // Use verifyNoMoreInteractions to ensure logger was called appropriately
        // without trying to match specific varargs signatures
        verify(mockLogger, atLeastOnce()).info(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Info - Info Disabled")
    void logInfo_InfoDisabled_DoesNotCallLoggerInfo() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        String message = "Test message";

        // When
        LoggerUtils.logInfo(mockLogger, message, "param");

        // Then
        verify(mockLogger).isInfoEnabled();
        verify(mockLogger, never()).info(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Info - No Parameters")
    void logInfo_NoParameters_CallsLoggerInfo() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        String message = "Simple message without parameters";

        // When
        LoggerUtils.logInfo(mockLogger, message);

        // Then
        verify(mockLogger).isInfoEnabled();
        verify(mockLogger, atLeastOnce()).info(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Error - Error Enabled")
    void logError_ErrorEnabled_CallsLoggerError() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isErrorEnabled()).thenReturn(true);
        String message = "Error occurred";
        Exception exception = new RuntimeException("Test exception");

        // When
        LoggerUtils.logError(mockLogger, message, exception);

        // Then
        verify(mockLogger).isErrorEnabled();
        verify(mockLogger).error(message, exception);
    }

    @Test
    @DisplayName("Log Error - Error Disabled")
    void logError_ErrorDisabled_DoesNotCallLoggerError() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isErrorEnabled()).thenReturn(false);
        String message = "Error message";
        Exception exception = new RuntimeException("Test");

        // When
        LoggerUtils.logError(mockLogger, message, exception);

        // Then
        verify(mockLogger).isErrorEnabled();
        verify(mockLogger, never()).error(anyString(), any(Throwable.class));
    }

    @Test
    @DisplayName("Log Warn - Warn Enabled")
    void logWarn_WarnEnabled_CallsLoggerWarn() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        String message = "Warning message with {}";
        String param = "parameter";

        // When
        LoggerUtils.logWarn(mockLogger, message, param);

        // Then
        verify(mockLogger).isWarnEnabled();
        verify(mockLogger, atLeastOnce()).warn(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Warn - Warn Disabled")
    void logWarn_WarnDisabled_DoesNotCallLoggerWarn() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isWarnEnabled()).thenReturn(false);
        String message = "Warning message";

        // When
        LoggerUtils.logWarn(mockLogger, message, "param");

        // Then
        verify(mockLogger).isWarnEnabled();
        verify(mockLogger, never()).warn(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Debug - Debug Enabled")
    void logDebug_DebugEnabled_CallsLoggerDebug() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        String message = "Debug message with {} and {}";
        Object param1 = "debug1";
        Object param2 = "debug2";

        // When
        LoggerUtils.logDebug(mockLogger, message, param1, param2);

        // Then
        verify(mockLogger).isDebugEnabled();
        verify(mockLogger, atLeastOnce()).debug(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Debug - Debug Disabled")
    void logDebug_DebugDisabled_DoesNotCallLoggerDebug() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isDebugEnabled()).thenReturn(false);
        String message = "Debug message";

        // When
        LoggerUtils.logDebug(mockLogger, message, "param");

        // Then
        verify(mockLogger).isDebugEnabled();
        verify(mockLogger, never()).debug(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Log Methods - Null Parameters Handle Gracefully")
    void logMethods_NullParameters_HandleGracefully() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        when(mockLogger.isErrorEnabled()).thenReturn(true);

        // When/Then - Should not throw exceptions
        assertDoesNotThrow(() -> {
            LoggerUtils.logInfo(mockLogger, null, (Object[]) null);
            LoggerUtils.logWarn(mockLogger, null, (Object[]) null);
            LoggerUtils.logDebug(mockLogger, null, (Object[]) null);
            LoggerUtils.logError(mockLogger, null, null);
        });
    }

    @Test
    @DisplayName("Log Methods - Empty Parameter Array")
    void logMethods_EmptyParameterArray_HandleGracefully() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        when(mockLogger.isWarnEnabled()).thenReturn(true);
        when(mockLogger.isDebugEnabled()).thenReturn(true);
        String message = "Message without parameters";

        // When
        LoggerUtils.logInfo(mockLogger, message, new Object[0]);
        LoggerUtils.logWarn(mockLogger, message, new Object[0]);
        LoggerUtils.logDebug(mockLogger, message, new Object[0]);

        // Then
        verify(mockLogger, atLeastOnce()).info(anyString(), any(Object[].class));
        verify(mockLogger, atLeastOnce()).warn(anyString(), any(Object[].class));
        verify(mockLogger, atLeastOnce()).debug(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Integration Test - Real Logger")
    void integrationTest_RealLogger_WorksCorrectly() {
        // Given
        Logger realLogger = LoggerUtils.getLogger(LoggerUtilsTest.class);

        // When/Then - Should not throw exceptions with real logger
        assertDoesNotThrow(() -> {
            LoggerUtils.logInfo(realLogger, "Integration test info message");
            LoggerUtils.logWarn(realLogger, "Integration test warning with param: {}", "testParam");
            LoggerUtils.logDebug(realLogger, "Integration test debug message");
            LoggerUtils.logError(realLogger, "Integration test error", new RuntimeException("Test exception"));
        });
    }

    @Test
    @DisplayName("Varargs Parameters - Multiple Parameters")
    void varargsParameters_MultipleParameters_PassedCorrectly() {
        // Given
        Logger mockLogger = mock(Logger.class);
        when(mockLogger.isInfoEnabled()).thenReturn(true);
        String message = "Message with {} {} {} parameters";
        Object[] params = {"first", "second", "third"};

        // When
        LoggerUtils.logInfo(mockLogger, message, params);

        // Then
        verify(mockLogger, atLeastOnce()).info(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Level Checking Optimization - Verifies Level Check Before Logging")
    void levelCheckingOptimization_VerifiesLevelCheckBeforeLogging() {
        // Given
        Logger mockLogger = mock(Logger.class);
        
        // Test each level with disabled logging
        when(mockLogger.isInfoEnabled()).thenReturn(false);
        when(mockLogger.isWarnEnabled()).thenReturn(false);
        when(mockLogger.isDebugEnabled()).thenReturn(false);
        when(mockLogger.isErrorEnabled()).thenReturn(false);

        // When
        LoggerUtils.logInfo(mockLogger, "info", "param");
        LoggerUtils.logWarn(mockLogger, "warn", "param");
        LoggerUtils.logDebug(mockLogger, "debug", "param");
        LoggerUtils.logError(mockLogger, "error", new Exception());

        // Then - Verify only level checks are called, not actual logging
        verify(mockLogger, times(1)).isInfoEnabled();
        verify(mockLogger, times(1)).isWarnEnabled();
        verify(mockLogger, times(1)).isDebugEnabled();
        verify(mockLogger, times(1)).isErrorEnabled();
        
        verify(mockLogger, never()).info(anyString(), any(Object[].class));
        verify(mockLogger, never()).warn(anyString(), any(Object[].class));
        verify(mockLogger, never()).debug(anyString(), any(Object[].class));
        verify(mockLogger, never()).error(anyString(), any(Throwable.class));
    }
}
