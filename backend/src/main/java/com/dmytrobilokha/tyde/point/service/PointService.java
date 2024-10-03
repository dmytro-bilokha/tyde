package com.dmytrobilokha.tyde.point.service;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class PointService {

    private static final int MAX_CAPACITY = 100;
    private static final Comparator<Point> POINT_COMPARATOR = Comparator.comparingLong(Point::getId);

    private final Map<Long, TreeSet<Point>> pointStorage = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PointService() { }

    public void registerPoint(Point point) {
        try {
            lock.writeLock().lock();
            var points = pointStorage.computeIfAbsent(
                    point.getGpsDeviceId(), gpsDeviceId -> new TreeSet<>(POINT_COMPARATOR));
            if (points.size() >= MAX_CAPACITY) {
                points.pollFirst();
            }
            points.add(point);
        } finally {
            lock.writeLock().unlock();
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
