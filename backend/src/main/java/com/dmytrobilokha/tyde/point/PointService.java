package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.CheckForNull;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* TODO:
Instead of this POC implement proper storage for points in the DB with separation by user device, etc.
 */
@ApplicationScoped
public class PointService {

    private static final int MAX_CAPACITY = 1_000_000;

    private final Deque<Point> pointStorage = new LinkedList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void registerPoint(Point point) {
        try {
            lock.writeLock().lock();
            if (pointStorage.size() >= MAX_CAPACITY) {
                pointStorage.removeFirst();
            }
            pointStorage.add(point);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @CheckForNull
    public Point getLastPoint() {
        try {
            lock.readLock().lock();
            return pointStorage.peekLast();
        } finally {
            lock.readLock().unlock();
        }
    }

}
