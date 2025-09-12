package com.xavier.sigasaasapi.common.application.dto;
import com.xavier.sigasaasapi.common.domain.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Base DTO class for all DTOs with audit fields.
 * Provides common fields like ID and timestamps.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDTO {
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}

/**
 * Generic response wrapper for API responses.
 * @param <T> the type of data
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now());
    }
}

/**
 * Paginated response wrapper for list responses.
 * @param <T> the type of content
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class PagedResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> from(Page<T> page) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setContent(page.getContent());
        response.setPageNumber(page.getPageNumber());
        response.setPageSize(page.getPageSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }
}