package com.dmytrobilokha.tyde.point.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class PointService {

    public static final int MAX_RETENTION_HOURS = 48;
    private static final int MAX_CAPACITY = 10000;
    private static final Duration MAX_DURATION = Duration.ofHours(MAX_RETENTION_HOURS);
    private static final Comparator<Point> POINT_COMPARATOR = Comparator.comparing(Point::getServerTimestamp);

    private final Map<Long, TreeSet<Point>> pointStorage = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PointService() { }

    public void registerPoint(Point point) {
        try {
            lock.writeLock().lock();
            var points = pointStorage.computeIfAbsent(
                    point.getGpsDeviceId(), gpsDeviceId -> new TreeSet<>(POINT_COMPARATOR));
            // Cleanup is triggered when the new point is to be registered,
            // this means last points could be stored in memory for longer than retention period.
            // Thus, access to them should be blocked on input request validation.
            cleanUpPoints(points);
            points.add(point);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void cleanUpPoints(NavigableSet<Point> points) {
        var earliestAllowed = Instant.now().minus(MAX_DURATION);
        points.headSet(getReferencePoint(earliestAllowed)).clear();
        while (points.size() >= MAX_CAPACITY) {
            points.pollFirst();
        }
    }

    private Point getReferencePoint(Instant timestamp) {
        var point = new Point();
        point.setServerTimestamp(timestamp);
        return point;
    }

    public List<Point> fetchLastPoints(long gpsDeviceId, Instant fromTimestamp) {
        var lastPoints = new ArrayList<Point>();
        try {
            lock.readLock().lock();
            var points = pointStorage.get(gpsDeviceId);
            if (points == null) {
                return lastPoints;
            }
            var referencePoint = getReferencePoint(fromTimestamp);
            lastPoints.addAll(points.tailSet(referencePoint, false));
            return lastPoints;
        } finally {
            lock.readLock().unlock();
        }
    }

}
