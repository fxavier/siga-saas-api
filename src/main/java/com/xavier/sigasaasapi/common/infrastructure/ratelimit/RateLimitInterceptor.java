package com.xavier.sigasaasapi.common.infrastructure.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * HTTP Interceptor for rate limiting.
 * Checks rate limits before processing requests.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
@ConditionalOnProperty(name = "app.rate-limiting.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String RATE_LIMIT_LIMIT = "X-RateLimit-Limit";
    private static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    private static final String RATE_LIMIT_RESET = "X-RateLimit-Reset";

    private final RateLimiter rateLimiter;

    @Autowired
    public RateLimitInterceptor(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String key = getClientKey(request);

        // Check rate limit
        if (!rateLimiter.allowRequest(key)) {
            // Rate limit exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader(RATE_LIMIT_LIMIT, "100");
            response.setHeader(RATE_LIMIT_REMAINING, "0");
            response.setHeader(RATE_LIMIT_RESET, String.valueOf(System.currentTimeMillis() + 60000));
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            return false;
        }

        // Add rate limit headers
        long remaining = rateLimiter.getAvailableTokens(key);
        response.setHeader(RATE_LIMIT_LIMIT, "100");
        response.setHeader(RATE_LIMIT_REMAINING, String.valueOf(remaining));
        response.setHeader(RATE_LIMIT_RESET, String.valueOf(System.currentTimeMillis() + 60000));

        return true;
    }

    private String getClientKey(HttpServletRequest request) {
        // Try to get API key from header
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return "api:" + apiKey;
        }

        // Try to get authenticated user
        String user = request.getRemoteUser();
        if (user != null && !user.isEmpty()) {
            return "user:" + user;
        }

        // Fallback to IP address
        String clientIp = getClientIpAddress(request);
        return "ip:" + clientIp;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Handle multiple IPs in X-Forwarded-For
                if (ip.contains(",")) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}