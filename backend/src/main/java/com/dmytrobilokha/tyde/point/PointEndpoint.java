package com.dmytrobilokha.tyde.point;

import com.dmytrobilokha.tyde.infra.websocket.JsonEncoder;
import jakarta.inject.Inject;
import jakarta.validation.constraints.Min;
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ServerEndpoint(value = "/point-endpoint", encoders = {JsonEncoder.class})
public class PointEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(PointEndpoint.class);

    private PointService pointService;
    private PointNotificationService pointNotificationService;

    public PointEndpoint() { }

    @Inject
    public PointEndpoint(
            PointService pointService,
            PointNotificationService pointNotificationService
    ) {
        this.pointService = pointService;
        this.pointNotificationService = pointNotificationService;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("User '{}' established connection", getUsername(session));
        pointNotificationService.subscribe(session);
    }

    private String getUsername(Session session) {
        var user = session.getUserPrincipal();
        return user == null ? "nobody" : user.getName();
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("User '{}' closed connection", getUsername(session));
        pointNotificationService.unsubscribe(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.info("User '{}' encountered an error", getUsername(session), throwable);
    }

    @OnMessage
    public void onMessage(Session session, @Min(1) int quantity) throws IOException, EncodeException {
        LOG.info("Received a message '{}' from the user '{}'", quantity, getUsername(session));
        var result = new LastPointsModel();
        result.setPoints(pointService.getLastPoints(quantity)
                .stream()
                .map(MappingUtil::toPointModel)
                .toList());
        session.getAsyncRemote().sendObject(result);
    }

}
