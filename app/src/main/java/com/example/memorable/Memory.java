package com.example.memorable;

import android.location.Location;
import android.net.Uri;

import java.util.Date;

public class Memory {

    String title, emoji, description, password;
    Date date;
    Location location;
    Uri imageUri;

    public Memory(String title, String emoji, String description, Date date, Location location, Uri imageUri) {
        this.title = title;
        this.emoji = emoji;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
    }

    public Memory(String title, String emoji, String description, String password, Date date, Location location, Uri imageUri) {
        this.title = title;
        this.emoji = emoji;
        this.description = description;
        this.password = password;
        this.date = date;
        this.location = location;
        this.imageUri = imageUri;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }
}
