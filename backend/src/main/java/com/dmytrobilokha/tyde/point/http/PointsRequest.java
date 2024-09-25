package com.dmytrobilokha.tyde.point.http;

import jakarta.validation.constraints.Min;

public class PointsRequest {

    @Min(0)
    private long gpsDeviceId;
    @Min(0)
    private long lastPointId;
    @Min(0)
    private int quantity;

    public long getGpsDeviceId() {
        return gpsDeviceId;
    }

    public void setGpsDeviceId(long gpsDeviceId) {
        this.gpsDeviceId = gpsDeviceId;
    }

    public long getLastPointId() {
        return lastPointId;
    }

    public void setLastPointId(long lastPointId) {
        this.lastPointId = lastPointId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "PointsRequest{" +
                "gpsDeviceId=" + gpsDeviceId +
                ", lastPointId=" + lastPointId +
                ", quantity=" + quantity +
                '}';
    }

}
