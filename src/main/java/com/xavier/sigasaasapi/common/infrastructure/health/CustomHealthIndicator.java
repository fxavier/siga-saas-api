package com.xavier.sigasaasapi.common.infrastructure.health;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom health indicator for application health checks.
 * Provides detailed health status of various application components.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;
    private final ExternalServiceHealthChecker externalServiceChecker;

    public CustomHealthIndicator(DataSource dataSource,
                                 ExternalServiceHealthChecker externalServiceChecker) {
        this.dataSource = dataSource;
        this.externalServiceChecker = externalServiceChecker;
    }

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();
        Health.Builder status = Health.up();

        // Check database connectivity
        checkDatabaseHealth(status, details);

        // Check external services
        checkExternalServices(status, details);

        // Check disk space
        checkDiskSpace(status, details);

        // Check memory usage
        checkMemoryUsage(status, details);

        // Add application version
        details.put("version", getApplicationVersion());
        details.put("timestamp", System.currentTimeMillis());

        return status.withDetails(details).build();
    }

    private void checkDatabaseHealth(Health.Builder status, Map<String, Object> details) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT 1");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                details.put("database", "UP");
                details.put("database.type", connection.getMetaData().getDatabaseProductName());
                details.put("database.version", connection.getMetaData().getDatabaseProductVersion());
            }
        } catch (Exception e) {
            status.down();
            details.put("database", "DOWN");
            details.put("database.error", e.getMessage());
        }
    }

    private void checkExternalServices(Health.Builder status, Map<String, Object> details) {
        Map<String, Boolean> serviceStatus = externalServiceChecker.checkAllServices();

        boolean allServicesUp = true;
        for (Map.Entry<String, Boolean> entry : serviceStatus.entrySet()) {
            String serviceName = "service." + entry.getKey();
            details.put(serviceName, entry.getValue() ? "UP" : "DOWN");

            if (!entry.getValue()) {
                allServicesUp = false;
            }
        }

        if (!allServicesUp) {
            status.down();
        }
    }

    private void checkDiskSpace(Health.Builder status, Map<String, Object> details) {
        java.io.File root = new java.io.File("/");
        long freeSpace = root.getFreeSpace();
        long totalSpace = root.getTotalSpace();
        long usedSpace = totalSpace - freeSpace;
        double usagePercent = (double) usedSpace / totalSpace * 100;

        details.put("disk.free", formatBytes(freeSpace));
        details.put("disk.total", formatBytes(totalSpace));
        details.put("disk.usage", String.format("%.2f%%", usagePercent));

        // Warning if disk usage is above 80%
        if (usagePercent > 80) {
            status.down();
            details.put("disk.warning", "Disk usage is above 80%");
        }
    }

    private void checkMemoryUsage(Health.Builder status, Map<String, Object> details) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        double usagePercent = (double) usedMemory / maxMemory * 100;

        details.put("memory.max", formatBytes(maxMemory));
        details.put("memory.total", formatBytes(totalMemory));
        details.put("memory.free", formatBytes(freeMemory));
        details.put("memory.used", formatBytes(usedMemory));
        details.put("memory.usage", String.format("%.2f%%", usagePercent));

        // Warning if memory usage is above 85%
        if (usagePercent > 85) {
            status.down();
            details.put("memory.warning", "Memory usage is above 85%");
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String getApplicationVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        return version != null ? version : "unknown";
    }

    /**
     * External service health checker interface.
     */
    @Component
    public static class ExternalServiceHealthChecker {

        public Map<String, Boolean> checkAllServices() {
            Map<String, Boolean> serviceStatus = new HashMap<>();

            // Check payment gateway
            serviceStatus.put("payment-gateway", checkPaymentGateway());

            // Check SMS service
            serviceStatus.put("sms-service", checkSmsService());

            // Check email service
            serviceStatus.put("email-service", checkEmailService());

            // Check cache service
            serviceStatus.put("cache", checkCacheService());

            return serviceStatus;
        }

        private boolean checkPaymentGateway() {
            // Implement actual health check for payment gateway
            return true;
        }

        private boolean checkSmsService() {
            // Implement actual health check for SMS service
            return true;
        }

        private boolean checkEmailService() {
            // Implement actual health check for email service
            return true;
        }

        private boolean checkCacheService() {
            // Implement actual health check for cache service
            return true;
        }
    }
}
