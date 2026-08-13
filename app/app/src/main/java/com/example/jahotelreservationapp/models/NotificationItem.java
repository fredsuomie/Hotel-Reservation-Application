package com.example.jahotelreservationapp.models;

public class NotificationItem {
    private String title;
    private String body;

    // Constructor
    public NotificationItem(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    // Setters (if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
