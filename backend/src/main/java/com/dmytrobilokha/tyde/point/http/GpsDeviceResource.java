package com.dmytrobilokha.tyde.point.http;

import com.dmytrobilokha.tyde.point.persistence.GpsDevice;
import com.dmytrobilokha.tyde.point.service.GpsDeviceAccessControlService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@RequestScoped
@Path("gps-device")
public class GpsDeviceResource {

    private GpsDeviceAccessControlService accessControlService;

    public GpsDeviceResource() {
        // CDI
    }

    @Inject
    public GpsDeviceResource(GpsDeviceAccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AvailableGpsDevicesModel getAvailableGpsDevices(@Context SecurityContext securityContext) {
        var devices = accessControlService.findUserReadableGpsDevices(securityContext.getUserPrincipal());
        var response = new AvailableGpsDevicesModel();
        response.setAvailableDevices(
                devices.stream()
                .map(this::mapToGpsDeviceModel)
                .toList()
        );
        return response;
    }

    private GpsDeviceModel mapToGpsDeviceModel(GpsDevice gpsDevice) {
        var gpsDeviceModel = new GpsDeviceModel();
        gpsDeviceModel.setId(gpsDevice.id());
        gpsDeviceModel.setDescription(gpsDevice.description());
        return gpsDeviceModel;
    }

}
