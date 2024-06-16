package com.dmytrobilokha.tyde.point.http;

import com.dmytrobilokha.tyde.point.PointMapper;
import com.dmytrobilokha.tyde.point.service.PointRegisteredEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ApplicationScoped
public class PointNotificationSender {

    private static final Logger LOG = LoggerFactory.getLogger(PointNotificationSender.class);
    private final Map<Long, Set<Session>> gpsDeviceSubscriberMap = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private PointMapper pointMapper;

    public PointNotificationSender() {
        // CDI
    }

    @Inject
    public PointNotificationSender(PointMapper pointMapper) {
        this.pointMapper = pointMapper;
    }

    public void subscribe(long gpsDeviceId, Session wsSession) {
        try {
            lock.writeLock().lock();
            var subscribers = gpsDeviceSubscriberMap.computeIfAbsent(gpsDeviceId, id -> new HashSet<>());
            subscribers.add(wsSession);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unsubscribe(long gpsDeviceId, Session wsSession) {
        try {
            lock.writeLock().lock();
            var subscribers = gpsDeviceSubscriberMap.get(gpsDeviceId);
            if (subscribers != null) {
                subscribers.remove(wsSession);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unsubscribeAll(Session wsSession) {
        try {
            lock.writeLock().lock();
            for (Set<Session> subscribers : gpsDeviceSubscriberMap.values()) {
                subscribers.remove(wsSession);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void notifyOnPointRegistered(@ObservesAsync PointRegisteredEvent pointRegisteredEvent) {
        try {
            lock.readLock().lock();
            var subscribers = gpsDeviceSubscriberMap.get(pointRegisteredEvent.getPoint().getGpsDeviceId());
            if (subscribers == null) {
                // nobody is subscribed
                return;
            }
            var lastPointsModel = new LastPointsModel();
            lastPointsModel.setPoints(List.of(pointMapper.mapToPointModel(pointRegisteredEvent.getPoint())));
            for (var wsSession : subscribers) {
                wsSession.getAsyncRemote().sendObject(lastPointsModel);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

}
