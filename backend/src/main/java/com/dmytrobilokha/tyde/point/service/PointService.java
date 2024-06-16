package com.dmytrobilokha.tyde.point.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class PointService {

    private static final int MAX_CAPACITY = 100;

    private final Map<Long, LinkedList<Point>> pointStorage = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Event<PointRegisteredEvent> event;

    public PointService() { }

    @Inject
    public PointService(Event<PointRegisteredEvent> event) {
        this.event = event;
    }

    public void registerPoint(Point point) {
        try {
            lock.writeLock().lock();
            var points = pointStorage.computeIfAbsent(point.getGpsDeviceId(), id -> new LinkedList<>());
            if (points.size() >= MAX_CAPACITY) {
                points.removeFirst();
            }
            points.add(point);
            event.fireAsync(new PointRegisteredEvent(point));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Point> getLastPoints(long gpsDeviceId, int quantity) {
        var lastPoints = new ArrayList<Point>();
        try {
            lock.readLock().lock();
            var points = pointStorage.get(gpsDeviceId);
            if (points == null) {
                return lastPoints;
            }
            for (var iterator = points.listIterator(points.size());
                 iterator.hasPrevious() && lastPoints.size() < quantity;) {
                var point = iterator.previous();
                lastPoints.add(point);
            }
            return lastPoints;
        } finally {
            lock.readLock().unlock();
        }
    }

}
