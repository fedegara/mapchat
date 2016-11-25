package com.example.federicogarateguy.mapchat;

/**
 * Created by federicogarateguy on 25/11/16.
 */

public class MapChatLocation {
    public double latitude;
    public double longitude;
    public String email;

    public MapChatLocation(double latitude, double longitude, String email) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
    }

    public MapChatLocation() {
    }

    @Override
    public String toString() {
        return email + ": lat " + latitude + " | long " + longitude;
    }
}
