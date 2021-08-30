package com.suthar.todo_location;


public class Event {
    private String id;
    private String type;
    private String title;
    private String description;
    private String date;
    private String time;
    private String address;
    private double latitude;
    private double longitude;
    private int accuracy;
    private int isDone;

    public Event(String id, String type, String title, String description, String date, String time, String address, double latitude, double longitude, int accuracy) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getisDone() {
        return isDone;
    }

    public void setisDone(int isDone) {
        this.isDone = isDone;
    }
}
