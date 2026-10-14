package com.rssb.assistance.dto;

public class PhotoUploadResponse {
    private Long photoId;
    private String filePath;
    private String url;

    public PhotoUploadResponse() {}

    public PhotoUploadResponse(Long photoId, String filePath, String url) {
        this.photoId = photoId;
        this.filePath = filePath;
        this.url = url;
    }

    public Long getPhotoId() { return photoId; }
    public void setPhotoId(Long photoId) { this.photoId = photoId; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
