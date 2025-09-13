package com.xavier.sigasaasapi.common.infrastructure.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for rate limiting.
 * Creates and configures rate limiting beans.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Configuration
public class RateLimitConfiguration {

    /**
     * Create RateLimitConfig bean with default rules.
     * @param properties the rate limit properties
     * @return configured RateLimitConfig
     */
    @Bean
    public RateLimiter.RateLimitConfig rateLimitConfig(RateLimitProperties properties) {
        RateLimiter.RateLimitConfig config = new RateLimiter.RateLimitConfig();

        // Configure default rule
        if (properties.getDefaultRule() != null) {
            config.addRule("default", new RateLimiter.RateLimitConfig.RateLimitRule(
                    properties.getDefaultRule().getCapacity(),
                    properties.getDefaultRule().getTokens(),
                    properties.getDefaultRule().getDuration()
            ));
        }

        // Configure API rule
        if (properties.getApiRule() != null) {
            config.addRule("api", new RateLimiter.RateLimitConfig.RateLimitRule(
                    properties.getApiRule().getCapacity(),
                    properties.getApiRule().getTokens(),
                    properties.getApiRule().getDuration()
            ));
        }

        // Configure Admin rule
        if (properties.getAdminRule() != null) {
            config.addRule("admin", new RateLimiter.RateLimitConfig.RateLimitRule(
                    properties.getAdminRule().getCapacity(),
                    properties.getAdminRule().getTokens(),
                    properties.getAdminRule().getDuration()
            ));
        }

        // Add any custom rules
        if (properties.getCustomRules() != null) {
            properties.getCustomRules().forEach((key, rule) -> {
                config.addRule(key, new RateLimiter.RateLimitConfig.RateLimitRule(
                        rule.getCapacity(),
                        rule.getTokens(),
                        rule.getDuration()
                ));
            });
        }

        return config;
    }

    /**
     * Create RateLimiter bean.
     * @param config the rate limit configuration
     * @return configured RateLimiter
     */
    @Bean
    public RateLimiter rateLimiter(RateLimiter.RateLimitConfig config) {
        return new RateLimiter(config);
    }

    /**
     * Create SlidingWindowRateLimiter bean.
     * @return new SlidingWindowRateLimiter instance
     */
    @Bean
    public RateLimiter.SlidingWindowRateLimiter slidingWindowRateLimiter() {
        return new RateLimiter.SlidingWindowRateLimiter();
    }

    /**
     * Create FixedWindowRateLimiter bean.
     * @return new FixedWindowRateLimiter instance
     */
    @Bean
    public RateLimiter.FixedWindowRateLimiter fixedWindowRateLimiter() {
        return new RateLimiter.FixedWindowRateLimiter();
    }

    /**
     * Configuration properties for rate limiting.
     */
    @Configuration
    @ConfigurationProperties(prefix = "app.rate-limiting")
    public static class RateLimitProperties {

        private boolean enabled = true;
        private RuleProperties defaultRule = new RuleProperties(100, 100, 60);
        private RuleProperties apiRule = new RuleProperties(1000, 1000, 60);
        private RuleProperties adminRule = new RuleProperties(10000, 10000, 60);
        private java.util.Map<String, RuleProperties> customRules;

        public static class RuleProperties {
            private int capacity;
            private int tokens;
            private long duration;

            public RuleProperties() {
            }

            public RuleProperties(int capacity, int tokens, long duration) {
                this.capacity = capacity;
                this.tokens = tokens;
                this.duration = duration;
            }

            public int getCapacity() {
                return capacity;
            }

            public void setCapacity(int capacity) {
                this.capacity = capacity;
            }

            public int getTokens() {
                return tokens;
            }

            public void setTokens(int tokens) {
                this.tokens = tokens;
            }

            public long getDuration() {
                return duration;
            }

            public void setDuration(long duration) {
                this.duration = duration;
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public RuleProperties getDefaultRule() {
            return defaultRule;
        }

        public void setDefaultRule(RuleProperties defaultRule) {
            this.defaultRule = defaultRule;
        }

        public RuleProperties getApiRule() {
            return apiRule;
        }

        public void setApiRule(RuleProperties apiRule) {
            this.apiRule = apiRule;
        }

        public RuleProperties getAdminRule() {
            return adminRule;
        }

        public void setAdminRule(RuleProperties adminRule) {
            this.adminRule = adminRule;
        }

        public java.util.Map<String, RuleProperties> getCustomRules() {
            return customRules;
        }

        public void setCustomRules(java.util.Map<String, RuleProperties> customRules) {
            this.customRules = customRules;
        }
    }
}