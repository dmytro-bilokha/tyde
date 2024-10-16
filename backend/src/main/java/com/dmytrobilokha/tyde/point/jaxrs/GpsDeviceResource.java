package com.dmytrobilokha.tyde.point.jaxrs;

import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import com.dmytrobilokha.tyde.point.PointMapper;
import com.dmytrobilokha.tyde.point.persistence.GpsDevice;
import com.dmytrobilokha.tyde.point.service.GpsDeviceAccessControlService;
import com.dmytrobilokha.tyde.point.service.PointService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.time.Instant;

@RequestScoped
@Path("gps-device")
public class GpsDeviceResource {

    private static final long YEAR_2124_MILLIS = 4882534016216L;

    private GpsDeviceAccessControlService accessControlService;
    private PointMapper pointMapper;
    private PointService pointService;

    public GpsDeviceResource() {
        // CDI
    }

    @Inject
    public GpsDeviceResource(PointMapper pointMapper,
                             PointService pointService,
                             GpsDeviceAccessControlService accessControlService) {
        this.pointMapper = pointMapper;
        this.pointService = pointService;
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

    @GET
    @Path("{gpsDeviceId}/point")
    @Produces(MediaType.APPLICATION_JSON)
    public LastPointsModel getLastPoints(
            // TODO: add proper error messages
            @PathParam("gpsDeviceId") @NotNull @Min(0) Long gpsDeviceId,
            @QueryParam("fromTimestamp") @NotNull @Min(0) @Max(YEAR_2124_MILLIS) Long fromTimestamp,
            @Context SecurityContext securityContext
    ) throws InvalidInputException {
        accessControlService.checkUserAccess(securityContext.getUserPrincipal(), gpsDeviceId);
        var response = new LastPointsModel();
        response.setPoints(pointService.fetchLastPoints(gpsDeviceId, Instant.ofEpochMilli(fromTimestamp))
                .stream()
                .map(pointMapper::mapToPointModel)
                .toList());
        return response;
    }

    private GpsDeviceModel mapToGpsDeviceModel(GpsDevice gpsDevice) {
        var gpsDeviceModel = new GpsDeviceModel();
        gpsDeviceModel.setId(gpsDevice.id());
        gpsDeviceModel.setDescription(gpsDevice.description());
        return gpsDeviceModel;
    }

}
