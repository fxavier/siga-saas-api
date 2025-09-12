package com.xavier.sigasaasapi.common.domain.pagination;

/**
 * PageRequest class for requesting a specific page.
 * Contains pagination parameters.
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */
public class PageRequest {
    private final int pageNumber;
    private final int pageSize;
    private final Sort sort;

    private PageRequest(int pageNumber, int pageSize, Sort sort) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize, null);
    }

    public static PageRequest of(int pageNumber, int pageSize, Sort sort) {
        return new PageRequest(pageNumber, pageSize, sort);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Sort getSort() {
        return sort;
    }

    public int getOffset() {
        return pageNumber * pageSize;
    }

    public PageRequest next() {
        return new PageRequest(pageNumber + 1, pageSize, sort);
    }

    public PageRequest previous() {
        return pageNumber == 0 ? this : new PageRequest(pageNumber - 1, pageSize, sort);
    }

    public PageRequest first() {
        return new PageRequest(0, pageSize, sort);
    }
}
