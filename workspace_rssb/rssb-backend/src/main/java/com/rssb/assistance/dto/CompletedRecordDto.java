package com.rssb.assistance.dto;

public class CompletedRecordDto {
    private String recordId;
    private String title;
    private String completedAt;

    public CompletedRecordDto() {
    }

    public CompletedRecordDto(String recordId, String title, String completedAt) {
        this.recordId = recordId;
        this.title = title;
        this.completedAt = completedAt;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
