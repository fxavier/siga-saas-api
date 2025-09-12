package com.xavier.sigasaasapi.common.domain.pagination;
/**
 * Page class representing a page of results.
 * Contains the content and metadata about the page.
 * @param <T> the type of content
 * @version 1.0
 * @since 2025-09-12
 * @author Xavier Nhagumbe
 */

import java.util.List;
import java.util.Objects;

public class Page<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    public boolean isFirst() {
        return pageNumber == 0;
    }

    public boolean isLast() {
        return pageNumber == totalPages - 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page<?> page = (Page<?>) o;
        return pageNumber == page.pageNumber &&
                pageSize == page.pageSize &&
                totalElements == page.totalElements &&
                Objects.equals(content, page.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, pageNumber, pageSize, totalElements);
    }
}

