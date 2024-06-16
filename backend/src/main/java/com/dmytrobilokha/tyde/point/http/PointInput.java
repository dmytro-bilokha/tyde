package com.dmytrobilokha.tyde.point.http;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

import java.math.BigDecimal;

public class PointInput {

    @FormParam("lat")
    @NotNull
    private BigDecimal lat;
    @FormParam("lon")
    @NotNull
    private BigDecimal lon;
    @FormParam("timestamp")
    @NotNull
    private Long timestamp;
    @FormParam("speed")
    @NotNull
    private BigDecimal speed;
    @FormParam("altitude")
    @NotNull
    private BigDecimal altitude;
    @FormParam("direction")
    @NotNull
    private BigDecimal direction;
    @FormParam("accuracy")
    @NotNull
    private BigDecimal accuracy;
    @FormParam("provider")
    @NotNull
    private String provider;
    @FormParam("token")
    @NotEmpty
    private String token;

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public BigDecimal getDirection() {
        return direction;
    }

    public void setDirection(BigDecimal direction) {
        this.direction = direction;
    }

    public BigDecimal getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(BigDecimal accuracy) {
        this.accuracy = accuracy;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
