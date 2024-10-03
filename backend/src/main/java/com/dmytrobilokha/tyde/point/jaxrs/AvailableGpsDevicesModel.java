package com.dmytrobilokha.tyde.point.jaxrs;

import java.util.ArrayList;
import java.util.List;

public class AvailableGpsDevicesModel {

    private List<GpsDeviceModel> availableDevices = new ArrayList<>();

    public List<GpsDeviceModel> getAvailableDevices() {
        return availableDevices;
    }

    public void setAvailableDevices(List<GpsDeviceModel> availableDevices) {
        this.availableDevices = availableDevices;
    }

}
