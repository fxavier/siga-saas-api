package com.xavier.sigasaasapi.common.infrastructure.logging;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods for automatic logging.
 * Methods annotated with @Loggable will have their execution logged.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * Log level for the method.
     * @return the log level
     */
    LogLevel level() default LogLevel.DEBUG;

    /**
     * Whether to log method arguments.
     * @return true if arguments should be logged
     */
    boolean logArgs() default true;

    /**
     * Whether to log method result.
     * @return true if result should be logged
     */
    boolean logResult() default true;

    /**
     * Whether to log execution time.
     * @return true if execution time should be logged
     */
    boolean logExecutionTime() default true;

    /**
     * Log level enumeration.
     */
    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }
}
