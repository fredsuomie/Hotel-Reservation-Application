package com.example.jahotelreservationapp.models;

import com.google.firebase.Timestamp;

public class Payment {
    private String id;
    private String userId;
    private String bookingId;
    private double amount;
    private String status;        // e.g., "completed", "failed", "refunded"
    private String paymentMethod; // e.g., "Credit Card", "UPI", etc.
    private Timestamp timestamp;

    // Empty constructor required for Firestore
    public Payment() { }

    public Payment(String id, String userId, String bookingId, double amount, String status, String paymentMethod, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
