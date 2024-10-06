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
    private static final Comparator<Point> POINT_COMPARATOR = Comparator.comparingLong(Point::getId);

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
        while (points.size() >= MAX_CAPACITY) {
            points.pollFirst();
        }
        var earliestAllowed = Instant.now().minus(MAX_DURATION);
        for (var iterator = points.iterator(); iterator.hasNext();) {
            var point = iterator.next();
            if (point.getServerTimestamp().isBefore(earliestAllowed)) {
                iterator.remove();
            } else {
                break;
            }
        }
    }

    public List<Point> getLastPoints(long gpsDeviceId, long lastId, int limit) {
        var lastPoints = new ArrayList<Point>();
        try {
            lock.readLock().lock();
            var points = pointStorage.get(gpsDeviceId);
            if (points == null) {
                return lastPoints;
            }
            var referencePoint = new Point();
            referencePoint.setId(lastId);
            for (var iterator = points.tailSet(referencePoint, false).descendingIterator();
                 iterator.hasNext() && lastPoints.size() < limit;) {
                var point = iterator.next();
                lastPoints.add(point);
            }
            return lastPoints;
        } finally {
            lock.readLock().unlock();
        }
    }

}
