package com.rssb.assistance.dto;

public class RegistrationResponse {
    private String id;
    private String message;

    public RegistrationResponse() {
    }

    public RegistrationResponse(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
