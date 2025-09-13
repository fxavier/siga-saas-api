package com.xavier.sigasaasapi.common.infrastructure.logging;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * AOP Aspect for structured logging across the application.
 * Logs method entry, exit, and execution time with correlation IDs.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Aspect
@Component
public class LoggingAspect {

    private static final String CORRELATION_ID = "correlationId";
    private static final String METHOD_NAME = "methodName";
    private static final String CLASS_NAME = "className";
    private static final String EXECUTION_TIME = "executionTime";

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("@within(org.springframework.stereotype.Service) || " +
            "@within(org.springframework.stereotype.Repository) || " +
            "@within(org.springframework.web.bind.annotation.RestController)")
    public void applicationComponents() {
        // Pointcut for application components
    }

    @Pointcut("@annotation(com.xavier.sigasaasapi.common.infrastructure.logging.Loggable)")
    public void loggableMethod() {
        // Pointcut for methods annotated with @Loggable
    }

    @Around("applicationComponents() || loggableMethod()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String correlationId = getOrCreateCorrelationId();

        // Set MDC context for structured logging
        MDC.put(CORRELATION_ID, correlationId);
        MDC.put(CLASS_NAME, className);
        MDC.put(METHOD_NAME, methodName);

        Instant startTime = Instant.now();

        try {
            // Log method entry
            if (logger.isDebugEnabled()) {
                logger.debug("Entering method: {}.{} with arguments: {}",
                        className,
                        methodName,
                        sanitizeArguments(joinPoint.getArgs()));
            }

            // Execute the method
            Object result = joinPoint.proceed();

            // Calculate execution time
            Duration executionTime = Duration.between(startTime, Instant.now());
            MDC.put(EXECUTION_TIME, String.valueOf(executionTime.toMillis()));

            // Log method exit
            if (logger.isDebugEnabled()) {
                logger.debug("Exiting method: {}.{} with result: {} in {}ms",
                        className,
                        methodName,
                        sanitizeResult(result),
                        executionTime.toMillis());
            }

            // Log performance warning if execution takes too long
            if (executionTime.toMillis() > 1000) {
                logger.warn("Slow method execution: {}.{} took {}ms",
                        className,
                        methodName,
                        executionTime.toMillis());
            }

            return result;

        } catch (Exception e) {
            // Calculate execution time
            Duration executionTime = Duration.between(startTime, Instant.now());
            MDC.put(EXECUTION_TIME, String.valueOf(executionTime.toMillis()));

            // Log error
            logger.error("Error in method: {}.{} after {}ms - Error: {}",
                    className,
                    methodName,
                    executionTime.toMillis(),
                    e.getMessage(),
                    e);

            throw e;

        } finally {
            // Clear MDC context
            MDC.remove(CLASS_NAME);
            MDC.remove(METHOD_NAME);
            MDC.remove(EXECUTION_TIME);
            // Keep correlation ID for the entire request
        }
    }

    private String getOrCreateCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID);
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
            MDC.put(CORRELATION_ID, correlationId);
        }
        return correlationId;
    }

    private String sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        try {
            // Filter out sensitive data
            return Arrays.stream(args)
                    .map(this::sanitizeObject)
                    .toList()
                    .toString();
        } catch (Exception e) {
            return "[Unable to serialize arguments]";
        }
    }

    private String sanitizeResult(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            return sanitizeObject(result);
        } catch (Exception e) {
            return "[Unable to serialize result]";
        }
    }

    private String sanitizeObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        // Check if object contains sensitive data
        String className = obj.getClass().getSimpleName();
        if (className.contains("Password") ||
                className.contains("Token") ||
                className.contains("Secret")) {
            return "[REDACTED]";
        }

        // Limit string length to avoid logging huge payloads
        String json = obj.toString();
        if (json.length() > 1000) {
            return json.substring(0, 1000) + "...[truncated]";
        }

        return json;
    }
}