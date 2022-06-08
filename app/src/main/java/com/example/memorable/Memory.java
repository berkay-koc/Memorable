package com.example.memorable;

public class Memory {
    String title, emoji, description, password, date, location, imageUri, id, isDeleted;

    public Memory(String id, String title, String emoji, String description, String date, String location, String imageUri, String isDeleted) {
        this.id = id;
        this.title = title;
        this.emoji = emoji;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.isDeleted = "0";
    }

    public Memory(String id, String title, String emoji, String description,  String date, String location, String imageUri, String password, String isDeleted) {
        this.title = title;
        this.emoji = emoji;
        this.description = description;
        this.password = password;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
        this.isDeleted = "0";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
