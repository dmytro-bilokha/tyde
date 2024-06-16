package com.dmytrobilokha.tyde.point.service;

public class PointRegisteredEvent {

    private final Point point;

    public PointRegisteredEvent(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

}
