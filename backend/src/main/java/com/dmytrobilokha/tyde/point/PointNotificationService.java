package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class PointNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(PointNotificationService.class);
    private final Set<Session> wsSessions = new CopyOnWriteArraySet<>();

    public void subscribe(Session wsSession) {
        wsSessions.add(wsSession);
    }

    public void unsubscribe(Session wsSession) {
        wsSessions.remove(wsSession);
    }

    public void notifyOnPointRegistered(@ObservesAsync PointRegisteredEvent pointRegisteredEvent) {
        var lastPointsModel = new LastPointsModel();
        lastPointsModel.setPoints(List.of(MappingUtil.toPointModel(pointRegisteredEvent.getPoint())));
        for (var wsSession : wsSessions) {
            try {
                wsSession.getBasicRemote().sendObject(lastPointsModel);
            } catch (IOException | EncodeException e) {
                LOG.error("Failed to notify client on point registered", e);
            }
        }
    }

}
