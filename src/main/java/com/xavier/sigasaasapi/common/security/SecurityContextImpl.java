package com.xavier.sigasaasapi.common.security;
import com.xavier.sigasaasapi.common.security.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Security implementation of SecurityContext.
 * Retrieves user information from Spring Security context.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Component
public class SecurityContextImpl implements SecurityContext {

    @Override
    public Optional<String> getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Long> getCurrentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        // This assumes the UserDetails implementation has a getUserId method
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return Optional.of(((CustomUserDetails) principal).getUserId());
        }

        return Optional.empty();
    }

    @Override
    public Set<String> getCurrentUserRoles() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Collections.emptySet();
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    @Override
    public boolean hasAnyRole(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).anyMatch(userRoles::contains);
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        Set<String> userRoles = getCurrentUserRoles();
        return Arrays.stream(roles).allMatch(userRoles::contains);
    }

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    @Override
    public Optional<String> getCurrentTenantId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return Optional.ofNullable(((CustomUserDetails) principal).getTenantId());
        }

        return Optional.empty();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Custom UserDetails interface for additional user information.
     */
    public interface CustomUserDetails extends UserDetails {
        Long getUserId();
        String getTenantId();
    }
}