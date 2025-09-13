package com.xavier.sigasaasapi.common.infrastructure.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rate limiting service using Token Bucket algorithm.
 * Custom implementation without external dependencies.
 * Provides configurable rate limiting per client/API key.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Service
public class RateLimiter {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitConfig config;

    @Autowired
    public RateLimiter(RateLimitConfig config) {
        this.config = config;
    }

    /**
     * Default constructor for testing.
     */
    protected RateLimiter() {
        this.config = new RateLimitConfig();
    }

    /**
     * Check if a request is allowed for the given key.
     * @param key the client identifier (IP, API key, user ID)
     * @return true if request is allowed
     */
    public boolean allowRequest(String key) {
        return getBucket(key).tryConsume(1);
    }

    /**
     * Check if a request is allowed and consume tokens.
     * @param key the client identifier
     * @param tokens number of tokens to consume
     * @return true if request is allowed
     */
    public boolean allowRequest(String key, int tokens) {
        return getBucket(key).tryConsume(tokens);
    }

    /**
     * Get available tokens for a key.
     * @param key the client identifier
     * @return number of available tokens
     */
    public long getAvailableTokens(String key) {
        return getBucket(key).getAvailableTokens();
    }

    /**
     * Reset the bucket for a key.
     * @param key the client identifier
     */
    public void resetBucket(String key) {
        buckets.remove(key);
    }

    /**
     * Clear all buckets.
     */
    public void clearAll() {
        buckets.clear();
    }

    private TokenBucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            RateLimitConfig.RateLimitRule rule = config.getRuleForKey(key);
            return new TokenBucket(rule.getCapacity(), rule.getTokens(), rule.getDuration());
        });
    }

    /**
     * Custom Token Bucket implementation.
     */
    public static class TokenBucket {
        private final int capacity;
        private final int refillTokens;
        private final long refillIntervalMillis;
        private final AtomicInteger availableTokens;
        private volatile long lastRefillTimestamp;
        private final ReentrantLock lock = new ReentrantLock();

        public TokenBucket(int capacity, int refillTokens, long refillIntervalSeconds) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillIntervalMillis = refillIntervalSeconds * 1000;
            this.availableTokens = new AtomicInteger(capacity);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public boolean tryConsume(int tokens) {
            lock.lock();
            try {
                refill();

                if (availableTokens.get() >= tokens) {
                    availableTokens.addAndGet(-tokens);
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        public long getAvailableTokens() {
            lock.lock();
            try {
                refill();
                return availableTokens.get();
            } finally {
                lock.unlock();
            }
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timeSinceLastRefill = now - lastRefillTimestamp;

            if (timeSinceLastRefill >= refillIntervalMillis) {
                int periodsToRefill = (int) (timeSinceLastRefill / refillIntervalMillis);
                int tokensToAdd = periodsToRefill * refillTokens;
                int newTokenCount = Math.min(capacity, availableTokens.get() + tokensToAdd);
                availableTokens.set(newTokenCount);
                lastRefillTimestamp = now;
            }
        }
    }

    /**
     * Sliding Window Rate Limiter implementation.
     * Alternative to Token Bucket for more precise rate limiting.
     */
    public static class SlidingWindowRateLimiter {
        private final Map<String, SlidingWindow> windows = new ConcurrentHashMap<>();

        public boolean allowRequest(String key, int limit, Duration windowDuration) {
            SlidingWindow window = windows.computeIfAbsent(key,
                    k -> new SlidingWindow(limit, windowDuration));
            return window.allowRequest();
        }

        public void resetKey(String key) {
            windows.remove(key);
        }

        public void clearAll() {
            windows.clear();
        }

        private static class SlidingWindow {
            private final Queue<Instant> requestTimestamps = new ConcurrentLinkedQueue<>();
            private final int limit;
            private final Duration windowDuration;
            private final ReentrantLock lock = new ReentrantLock();

            public SlidingWindow(int limit, Duration windowDuration) {
                this.limit = limit;
                this.windowDuration = windowDuration;
            }

            public boolean allowRequest() {
                lock.lock();
                try {
                    Instant now = Instant.now();
                    Instant windowStart = now.minus(windowDuration);

                    // Remove timestamps outside the window
                    while (!requestTimestamps.isEmpty() &&
                            requestTimestamps.peek().isBefore(windowStart)) {
                        requestTimestamps.poll();
                    }

                    // Check if we can accept the request
                    if (requestTimestamps.size() < limit) {
                        requestTimestamps.offer(now);
                        return true;
                    }

                    return false;
                } finally {
                    lock.unlock();
                }
            }

            public int getCurrentCount() {
                lock.lock();
                try {
                    Instant now = Instant.now();
                    Instant windowStart = now.minus(windowDuration);

                    // Clean old timestamps
                    while (!requestTimestamps.isEmpty() &&
                            requestTimestamps.peek().isBefore(windowStart)) {
                        requestTimestamps.poll();
                    }

                    return requestTimestamps.size();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * Configuration for rate limiting rules.
     */
    public static class RateLimitConfig {
        private final Map<String, RateLimitRule> rules = new ConcurrentHashMap<>();

        public RateLimitConfig() {
            // Default rule: 100 requests per minute
            rules.put("default", new RateLimitRule(100, 100, 60));
            // API rule: 1000 requests per minute
            rules.put("api", new RateLimitRule(1000, 1000, 60));
            // Admin rule: 10000 requests per minute
            rules.put("admin", new RateLimitRule(10000, 10000, 60));
        }

        public RateLimitRule getRuleForKey(String key) {
            return rules.getOrDefault(key, rules.get("default"));
        }

        public void addRule(String key, RateLimitRule rule) {
            rules.put(key, rule);
        }

        public static class RateLimitRule {
            private final int capacity;
            private final int tokens;
            private final long duration;

            public RateLimitRule(int capacity, int tokens, long duration) {
                this.capacity = capacity;
                this.tokens = tokens;
                this.duration = duration;
            }

            public int getCapacity() {
                return capacity;
            }

            public int getTokens() {
                return tokens;
            }

            public long getDuration() {
                return duration;
            }
        }
    }

    /**
     * Fixed Window Rate Limiter - simpler alternative implementation.
     */
    public static class FixedWindowRateLimiter {
        private final Map<String, FixedWindow> windows = new ConcurrentHashMap<>();

        public boolean allowRequest(String key, int limit, long windowSizeMillis) {
            FixedWindow window = windows.computeIfAbsent(key,
                    k -> new FixedWindow(limit, windowSizeMillis));
            return window.allowRequest();
        }

        private static class FixedWindow {
            private final int limit;
            private final long windowSizeMillis;
            private final AtomicInteger counter = new AtomicInteger(0);
            private volatile long windowStart;
            private final ReentrantLock lock = new ReentrantLock();

            public FixedWindow(int limit, long windowSizeMillis) {
                this.limit = limit;
                this.windowSizeMillis = windowSizeMillis;
                this.windowStart = System.currentTimeMillis();
            }

            public boolean allowRequest() {
                lock.lock();
                try {
                    long now = System.currentTimeMillis();

                    // Check if we need to reset the window
                    if (now - windowStart >= windowSizeMillis) {
                        counter.set(0);
                        windowStart = now;
                    }

                    // Check if we can accept the request
                    if (counter.get() < limit) {
                        counter.incrementAndGet();
                        return true;
                    }

                    return false;
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}