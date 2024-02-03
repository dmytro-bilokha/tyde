package com.dmytrobilokha.tyde.point;

import jakarta.ws.rs.FormParam;

public class PointInput {

    @FormParam("lat")
    private String lat;
    @FormParam("lon")
    private String lon;
    @FormParam("timestamp")
    private Long timestamp;
    @FormParam("speed")
    private String speed;
    @FormParam("altitude")
    private String altitude;
    @FormParam("direction")
    private String direction;
    @FormParam("accuracy")
    private String accuracy;
    @FormParam("provider")
    private String provider;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}
