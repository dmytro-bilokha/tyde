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
import java.time.temporal.ChronoUnit;

@RequestScoped
@Path("gps-device")
public class GpsDeviceResource {

    private static final long YEAR_2124_MILLIS = 4882534016216L;
    private static final long MAX_RETENTION_MINUTES = PointService.MAX_RETENTION_HOURS * 60;
    private static final String GPS_DEVICE_PARAM = "gpsDeviceId";
    private static final String FROM_TIME_PARAM = "fromTimestamp";
    private static final String LAST_MINUTES_PARAM = "lastMinutes";

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
            @PathParam(GPS_DEVICE_PARAM)
            @NotNull(message = GPS_DEVICE_PARAM + " must be provided")
            @Min(value = 0, message = GPS_DEVICE_PARAM + " must not be negative") Long gpsDeviceId,
            @QueryParam(FROM_TIME_PARAM)
            @NotNull(message = FROM_TIME_PARAM + " must be provided")
            @Min(value = 0, message = FROM_TIME_PARAM + " must not be negative")
            @Max(value = YEAR_2124_MILLIS,
                 message = FROM_TIME_PARAM + " must not be higher than " + YEAR_2124_MILLIS) Long fromTimestamp,
            @QueryParam(LAST_MINUTES_PARAM)
            @NotNull(message = LAST_MINUTES_PARAM + " must be provided")
            @Min(value = 0, message = LAST_MINUTES_PARAM + " must not be negative")
            @Max(value = MAX_RETENTION_MINUTES,
                 message = LAST_MINUTES_PARAM + " must not be higher than " + MAX_RETENTION_MINUTES) Long lastMinutes,
            @Context SecurityContext securityContext
    ) throws InvalidInputException {
        accessControlService.checkUserAccess(securityContext.getUserPrincipal(), gpsDeviceId);
        var response = new LastPointsModel();
        var lastMinutesInstant = Instant.now().minus(lastMinutes, ChronoUnit.MINUTES);
        var fromTimestampInstant = Instant.ofEpochMilli(fromTimestamp);
        var effectiveInstant = fromTimestampInstant.isAfter(lastMinutesInstant)
                ? fromTimestampInstant
                : lastMinutesInstant;
        response.setPoints(pointService.fetchLastPoints(gpsDeviceId, effectiveInstant)
                .stream()
                .map(pointMapper::mapToPointModel)
                .toList());
        response.setEarliestPossibleTimestamp(lastMinutesInstant.toEpochMilli());
        return response;
    }

    private GpsDeviceModel mapToGpsDeviceModel(GpsDevice gpsDevice) {
        var gpsDeviceModel = new GpsDeviceModel();
        gpsDeviceModel.setId(gpsDevice.id());
        gpsDeviceModel.setDescription(gpsDevice.description());
        return gpsDeviceModel;
    }

}
