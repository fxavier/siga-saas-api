package com.xavier.sigasaasapi.common.infrastructure.metrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom metrics for application-specific monitoring.
 * Provides counters, gauges, and timers for business metrics.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    // Business metrics
    private final Counter userRegistrations;
    private final Counter loginAttempts;
    private final Counter failedLoginAttempts;
    private final Counter apiCalls;
    private final Counter apiErrors;

    // Performance metrics
    private final Timer databaseQueryTime;
    private final Timer apiResponseTime;

    // Gauge metrics
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    private final AtomicInteger pendingTasks = new AtomicInteger(0);

    public CustomMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Initialize counters
        this.userRegistrations = Counter.builder("user.registrations")
                .description("Total number of user registrations")
                .register(meterRegistry);

        this.loginAttempts = Counter.builder("user.login.attempts")
                .description("Total number of login attempts")
                .register(meterRegistry);

        this.failedLoginAttempts = Counter.builder("user.login.failed")
                .description("Total number of failed login attempts")
                .register(meterRegistry);

        this.apiCalls = Counter.builder("api.calls")
                .description("Total number of API calls")
                .register(meterRegistry);

        this.apiErrors = Counter.builder("api.errors")
                .description("Total number of API errors")
                .register(meterRegistry);

        // Initialize timers
        this.databaseQueryTime = Timer.builder("database.query.time")
                .description("Time taken for database queries")
                .register(meterRegistry);

        this.apiResponseTime = Timer.builder("api.response.time")
                .description("API response time")
                .register(meterRegistry);

        // Initialize gauges
        Gauge.builder("users.active", activeUsers, AtomicInteger::get)
                .description("Number of currently active users")
                .register(meterRegistry);

        Gauge.builder("tasks.pending", pendingTasks, AtomicInteger::get)
                .description("Number of pending tasks")
                .register(meterRegistry);
    }

    // Counter methods
    public void incrementUserRegistrations() {
        userRegistrations.increment();
    }

    public void incrementLoginAttempts(boolean success) {
        loginAttempts.increment();
        if (!success) {
            failedLoginAttempts.increment();
        }
    }

    public void incrementApiCalls() {
        apiCalls.increment();
    }

    public void incrementApiErrors() {
        apiErrors.increment();
    }

    // Timer methods
    public Timer.Sample startDatabaseQuery() {
        return Timer.start(meterRegistry);
    }

    public void recordDatabaseQuery(Timer.Sample sample) {
        sample.stop(databaseQueryTime);
    }

    public Timer.Sample startApiCall() {
        return Timer.start(meterRegistry);
    }

    public void recordApiCall(Timer.Sample sample) {
        sample.stop(apiResponseTime);
    }

    // Gauge methods
    public void setActiveUsers(int count) {
        activeUsers.set(count);
    }

    public void incrementActiveUsers() {
        activeUsers.incrementAndGet();
    }

    public void decrementActiveUsers() {
        activeUsers.decrementAndGet();
    }

    public void setPendingTasks(int count) {
        pendingTasks.set(count);
    }

    public void incrementPendingTasks() {
        pendingTasks.incrementAndGet();
    }

    public void decrementPendingTasks() {
        pendingTasks.decrementAndGet();
    }

    // Custom metric with tags
    public void recordBusinessOperation(String operation, String result, long duration) {
        Timer.builder("business.operation")
                .description("Business operation execution time")
                .tag("operation", operation)
                .tag("result", result)
                .register(meterRegistry)
                .record(java.time.Duration.ofMillis(duration));
    }

    public void recordEntityOperation(String entity, String operation) {
        Counter.builder("entity.operations")
                .description("Entity operation count")
                .tag("entity", entity)
                .tag("operation", operation)
                .register(meterRegistry)
                .increment();
    }
}