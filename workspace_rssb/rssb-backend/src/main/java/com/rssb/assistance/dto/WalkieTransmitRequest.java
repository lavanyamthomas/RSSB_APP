package com.rssb.assistance.dto;

import jakarta.validation.constraints.NotBlank;

public class WalkieTransmitRequest {

    @NotBlank(message = "Channel is required")
    private String channel;

    @NotBlank(message = "Message is required")
    private String message;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
