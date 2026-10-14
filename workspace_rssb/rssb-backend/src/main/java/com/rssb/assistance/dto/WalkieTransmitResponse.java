package com.rssb.assistance.dto;

public class WalkieTransmitResponse {
    private String id;
    private String channel;
    private String timestamp;
    private String status;

    public WalkieTransmitResponse() {
    }

    public WalkieTransmitResponse(String id, String channel, String timestamp, String status) {
        this.id = id;
        this.channel = channel;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
