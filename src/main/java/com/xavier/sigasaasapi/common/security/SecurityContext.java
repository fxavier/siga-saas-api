package com.xavier.sigasaasapi.common.security;
import java.util.Optional;
import java.util.Set;

/**
 * Security context interface for accessing current user information.
 * Provides methods to retrieve authenticated user details.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public interface SecurityContext {

    /**
     * Get the current authenticated username.
     * @return Optional containing username if authenticated
     */
    Optional<String> getCurrentUsername();

    /**
     * Get the current authenticated user ID.
     * @return Optional containing user ID if authenticated
     */
    Optional<Long> getCurrentUserId();

    /**
     * Get the current user's roles.
     * @return Set of role names
     */
    Set<String> getCurrentUserRoles();

    /**
     * Check if the current user has a specific role.
     * @param role the role to check
     * @return true if user has the role
     */
    boolean hasRole(String role);

    /**
     * Check if the current user has any of the specified roles.
     * @param roles the roles to check
     * @return true if user has any of the roles
     */
    boolean hasAnyRole(String... roles);

    /**
     * Check if the current user has all of the specified roles.
     * @param roles the roles to check
     * @return true if user has all the roles
     */
    boolean hasAllRoles(String... roles);

    /**
     * Check if the current user is authenticated.
     * @return true if authenticated
     */
    boolean isAuthenticated();

    /**
     * Get the current tenant ID for multi-tenant scenarios.
     * @return Optional containing tenant ID if applicable
     */
    Optional<String> getCurrentTenantId();
}