package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* TODO:
Instead of this POC implement proper storage for points in the DB with separation by user device, etc.
 */
@ApplicationScoped
public class PointService {

    private static final int MAX_CAPACITY = 1_000_000;

    private final LinkedList<Point> pointStorage = new LinkedList<>();
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
            if (pointStorage.size() >= MAX_CAPACITY) {
                pointStorage.removeFirst();
            }
            pointStorage.add(point);
            event.fireAsync(new PointRegisteredEvent(point));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<Point> getLastPoints(int quantity) {
        var lastPoints = new ArrayList<Point>();
        try {
            lock.readLock().lock();
            for (var iterator = pointStorage.listIterator(pointStorage.size());
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
