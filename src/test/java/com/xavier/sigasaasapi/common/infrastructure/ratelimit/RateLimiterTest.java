package com.xavier.sigasaasapi.common.infrastructure.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for RateLimiter.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
class RateLimiterTest {

    private RateLimiter rateLimiter;
    private RateLimiter.RateLimitConfig config;

    @BeforeEach
    void setUp() {
        config = new RateLimiter.RateLimitConfig();
        // Set a lower limit for testing (5 requests per second)
        config.addRule("test", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        rateLimiter = new RateLimiter(config);
    }

    @Test
    void allowRequest_shouldAllowRequestsWithinLimit() {
        // Given
        String key = "test-user";

        // Create a rate limiter with test config
        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("test-user", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When/Then - Should allow first 5 requests
        for (int i = 0; i < 5; i++) {
            assertThat(testLimiter.allowRequest(key))
                    .as("Request %d should be allowed", i + 1)
                    .isTrue();
        }

        // Should deny 6th request
        assertThat(testLimiter.allowRequest(key))
                .as("6th request should be denied")
                .isFalse();
    }

    @Test
    void allowRequest_withTokens_shouldConsumeMultipleTokens() {
        // Given
        String key = "test-user";
        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("test-user", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When
        boolean allowed = testLimiter.allowRequest(key, 3);

        // Then
        assertThat(allowed).isTrue();
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(2);
    }

    @Test
    void allowRequest_differentKeys_shouldHaveSeparateBuckets() {
        // Given
        String key1 = "user1";
        String key2 = "user2";

        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("user1", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        testConfig.addRule("user2", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When - Exhaust bucket for key1
        for (int i = 0; i < 5; i++) {
            testLimiter.allowRequest(key1);
        }

        // Then - key2 should still have all tokens
        assertThat(testLimiter.allowRequest(key2)).isTrue();
        assertThat(testLimiter.getAvailableTokens(key2)).isEqualTo(4);

        // And key1 should be exhausted
        assertThat(testLimiter.allowRequest(key1)).isFalse();
    }

    @Test
    void getAvailableTokens_shouldReturnCorrectCount() {
        // Given
        String key = "test-user";
        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("test-user", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(5);

        testLimiter.allowRequest(key);
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(4);

        testLimiter.allowRequest(key, 2);
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(2);
    }

    @Test
    void resetBucket_shouldResetTokensForKey() {
        // Given
        String key = "test-user";
        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("test-user", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When - Consume some tokens
        testLimiter.allowRequest(key, 3);
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(2);

        // Reset bucket
        testLimiter.resetBucket(key);

        // Then - Should have full tokens again
        assertThat(testLimiter.getAvailableTokens(key)).isEqualTo(5);
    }

    @Test
    void clearAll_shouldResetAllBuckets() {
        // Given
        String key1 = "user1";
        String key2 = "user2";

        RateLimiter.RateLimitConfig testConfig = new RateLimiter.RateLimitConfig();
        testConfig.addRule("user1", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        testConfig.addRule("user2", new RateLimiter.RateLimitConfig.RateLimitRule(5, 5, 1));
        RateLimiter testLimiter = new RateLimiter(testConfig);

        // When - Consume tokens for both keys
        testLimiter.allowRequest(key1, 3);
        testLimiter.allowRequest(key2, 2);

        assertThat(testLimiter.getAvailableTokens(key1)).isEqualTo(2);
        assertThat(testLimiter.getAvailableTokens(key2)).isEqualTo(3);

        // Clear all
        testLimiter.clearAll();

        // Then - Both should have full tokens
        assertThat(testLimiter.getAvailableTokens(key1)).isEqualTo(5);
        assertThat(testLimiter.getAvailableTokens(key2)).isEqualTo(5);
    }

    @Test
    void rateLimitConfig_shouldProvideDefaultRules() {
        // Given
        RateLimiter.RateLimitConfig defaultConfig = new RateLimiter.RateLimitConfig();

        // When
        RateLimiter.RateLimitConfig.RateLimitRule defaultRule = defaultConfig.getRuleForKey("default");
        RateLimiter.RateLimitConfig.RateLimitRule apiRule = defaultConfig.getRuleForKey("api");
        RateLimiter.RateLimitConfig.RateLimitRule adminRule = defaultConfig.getRuleForKey("admin");

        // Then
        assertThat(defaultRule.getCapacity()).isEqualTo(100);
        assertThat(apiRule.getCapacity()).isEqualTo(1000);
        assertThat(adminRule.getCapacity()).isEqualTo(10000);
    }

    @Test
    void rateLimitConfig_unknownKey_shouldReturnDefaultRule() {
        // Given
        RateLimiter.RateLimitConfig defaultConfig = new RateLimiter.RateLimitConfig();

        // When
        RateLimiter.RateLimitConfig.RateLimitRule rule = defaultConfig.getRuleForKey("unknown");

        // Then
        assertThat(rule.getCapacity()).isEqualTo(100);
        assertThat(rule.getTokens()).isEqualTo(100);
        assertThat(rule.getDuration()).isEqualTo(60);
    }

    @Test
    void allowRequest_afterRefill_shouldAllowMoreRequests() throws InterruptedException {
        // Given - Create config with 1 second refill
        RateLimiter.RateLimitConfig fastConfig = new RateLimiter.RateLimitConfig();
        fastConfig.addRule("fast-user", new RateLimiter.RateLimitConfig.RateLimitRule(2, 2, 1));
        RateLimiter fastLimiter = new RateLimiter(fastConfig);
        String key = "fast-user";

        // When - Consume all tokens
        assertThat(fastLimiter.allowRequest(key)).isTrue();
        assertThat(fastLimiter.allowRequest(key)).isTrue();
        assertThat(fastLimiter.allowRequest(key)).isFalse();

        // Wait for refill
        Thread.sleep(1100);

        // Then - Should allow more requests after refill
        assertThat(fastLimiter.allowRequest(key)).isTrue();
    }

    @Test
    void slidingWindowRateLimiter_shouldLimitRequestsInWindow() {
        // Given
        RateLimiter.SlidingWindowRateLimiter limiter = new RateLimiter.SlidingWindowRateLimiter();
        String key = "test-key";
        int limit = 3;
        Duration window = Duration.ofSeconds(1);

        // When/Then - Should allow first 3 requests
        for (int i = 0; i < limit; i++) {
            assertThat(limiter.allowRequest(key, limit, window))
                    .as("Request %d should be allowed", i + 1)
                    .isTrue();
        }

        // Should deny 4th request
        assertThat(limiter.allowRequest(key, limit, window))
                .as("4th request should be denied")
                .isFalse();
    }

    @Test
    void fixedWindowRateLimiter_shouldResetAfterWindow() throws InterruptedException {
        // Given
        RateLimiter.FixedWindowRateLimiter limiter = new RateLimiter.FixedWindowRateLimiter();
        String key = "test-key";
        int limit = 2;
        long windowMillis = 500;

        // When - Use all tokens
        assertThat(limiter.allowRequest(key, limit, windowMillis)).isTrue();
        assertThat(limiter.allowRequest(key, limit, windowMillis)).isTrue();
        assertThat(limiter.allowRequest(key, limit, windowMillis)).isFalse();

        // Wait for window to reset
        Thread.sleep(600);

        // Then - Should allow requests again
        assertThat(limiter.allowRequest(key, limit, windowMillis)).isTrue();
    }
}