package com.dmytrobilokha.tyde.point;

import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import com.dmytrobilokha.tyde.point.jaxrs.PointInput;
import com.dmytrobilokha.tyde.point.jaxrs.PointModel;
import com.dmytrobilokha.tyde.point.persistence.GpsDeviceRepository;
import com.dmytrobilokha.tyde.point.service.Point;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class PointMapper {

    @SuppressWarnings("PMD.FieldNamingConventions")
    private static final AtomicLong lastPointId = new AtomicLong();

    private GpsDeviceRepository gpsDeviceRepository;

    public PointMapper() {
        // CDI
    }

    @Inject
    public PointMapper(GpsDeviceRepository gpsDeviceRepository) {
        this.gpsDeviceRepository = gpsDeviceRepository;
    }

    public PointModel mapToPointModel(Point point) {
        var pointModel = new PointModel();
        pointModel.setId(point.getId());
        pointModel.setLat(point.getLat());
        pointModel.setLon(point.getLon());
        pointModel.setTimestamp(point.getServerTimestamp().toEpochMilli());
        pointModel.setSpeed(point.getSpeed());
        pointModel.setAltitude(point.getAltitude());
        pointModel.setDirection(point.getDirection());
        pointModel.setAccuracy(point.getAccuracy());
        return pointModel;
    }

    public Point mapToPoint(PointInput pointInput) throws InvalidInputException {
        var gpsDevice = gpsDeviceRepository.findDeviceByToken(pointInput.getToken());
        if (gpsDevice == null) {
            throw new InvalidInputException("Unknown GPS device token provided");
        }
        var point = new Point();
        point.setId(lastPointId.incrementAndGet());
        point.setGpsDeviceId(gpsDevice.id());
        point.setLat(pointInput.getLat());
        point.setLon(pointInput.getLon());
        point.setClientTimestamp(Instant.ofEpochSecond(pointInput.getTimestamp()));
        point.setSpeed(pointInput.getSpeed());
        point.setAltitude(pointInput.getAltitude());
        point.setDirection(pointInput.getDirection());
        point.setAccuracy(pointInput.getAccuracy());
        point.setProvider(pointInput.getProvider());
        point.setServerTimestamp(Instant.now());
        return point;
    }

}
