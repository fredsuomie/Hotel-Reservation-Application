package com.example.jahotelreservationapp.models;


import com.google.firebase.Timestamp;

public class Booking {
    private String id;
    private String userId;
    private String roomId;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; // e.g., "pending", "approved", "rejected"

    // Empty constructor required for Firestore
    public Booking() { }

    public Booking(String id, String userId, String roomId, Timestamp startDate, Timestamp endDate, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
