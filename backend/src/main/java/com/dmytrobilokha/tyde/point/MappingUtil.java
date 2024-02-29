package com.dmytrobilokha.tyde.point;

import java.time.Instant;

public final class MappingUtil {

    private MappingUtil() {
        // no instance
    }

    public static PointModel toPointModel(Point point) {
        var pointModel = new PointModel();
        pointModel.setLat(point.getLat());
        pointModel.setLon(point.getLon());
        pointModel.setTimestamp(point.getClientTimestamp());
        pointModel.setSpeed(point.getSpeed());
        pointModel.setAltitude(point.getAltitude());
        pointModel.setDirection(point.getDirection());
        pointModel.setAccuracy(point.getAccuracy());
        return pointModel;
    }

    public static Point toPoint(PointInput pointInput) {
        var point = new Point();
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
