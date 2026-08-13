package com.example.jahotelreservationapp.models;

public class Room {
    private String id;          // Firestore document ID
    private String roomNumber;
    private String type;
    private double price;
    private String status;
    private String imageName;   // New field for image name or URL

    // Empty constructor required for Firestore
    public Room() { }

    public Room(String id, String roomNumber, String type, double price, String status, String imageName) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.imageName = imageName;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getImageName() {
        return imageName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
