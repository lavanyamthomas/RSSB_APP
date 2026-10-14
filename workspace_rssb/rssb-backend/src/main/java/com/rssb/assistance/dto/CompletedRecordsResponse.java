package com.rssb.assistance.dto;

import java.util.List;

public class CompletedRecordsResponse {
    private List<CompletedRecordDto> records;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sortBy;
    private String sortDir;

    public CompletedRecordsResponse() {
    }

    public CompletedRecordsResponse(List<CompletedRecordDto> records) {
        this.records = records;
    }

    public CompletedRecordsResponse(List<CompletedRecordDto> records, int page, int size, long totalElements, int totalPages, String sortBy, String sortDir) {
        this.records = records;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    public List<CompletedRecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<CompletedRecordDto> records) {
        this.records = records;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
