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

    private static final int MAX_CAPACITY = 10000;
    private static final Duration MAX_DURATION = Duration.ofHours(48L);
    private static final Comparator<Point> POINT_COMPARATOR = Comparator.comparing(Point::getServerTimestamp);

    private final Map<Long, TreeSet<Point>> pointStorage = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PointService() { }

    public void registerPoint(Point point) {
        try {
            lock.writeLock().lock();
            var points = pointStorage.computeIfAbsent(
                    point.getGpsDeviceId(), gpsDeviceId -> new TreeSet<>(POINT_COMPARATOR));
            cleanUpPoints(points);
            points.add(point);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void cleanUpPoints(NavigableSet<Point> points) {
        // TODO: to have proper retention policy, this should be executed by timer, not only when a point is added
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
