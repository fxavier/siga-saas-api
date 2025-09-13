package com.xavier.sigasaasapi.common.infrastructure.web;

import com.xavier.sigasaasapi.common.infrastructure.ratelimit.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 * Registers interceptors and configures web-related components.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired(required = false)
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add rate limit interceptor if available
        if (rateLimitInterceptor != null) {
            registry.addInterceptor(rateLimitInterceptor)
                    .addPathPatterns("/api/**")
                    .excludePathPatterns(
                            "/api/v1/auth/login",
                            "/api/v1/auth/register",
                            "/api/v1/public/**",
                            "/actuator/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    );
        }
    }
}