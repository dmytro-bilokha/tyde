package com.dmytrobilokha.tyde.point.http;

import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import com.dmytrobilokha.tyde.infra.websocket.JsonEncoder;
import com.dmytrobilokha.tyde.point.PointMapper;
import com.dmytrobilokha.tyde.point.service.GpsDeviceAccessControlService;
import com.dmytrobilokha.tyde.point.service.PointService;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ServerEndpoint(value = "/point-endpoint", encoders = {JsonEncoder.class})
public class PointEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(PointEndpoint.class);
    private static final String PING_MESSAGE = "PING";

    private PointService pointService;
    private PointNotificationSender pointNotificationSender;
    private PointMapper pointMapper;
    private GpsDeviceAccessControlService accessControlService;

    public PointEndpoint() { }

    @Inject
    public PointEndpoint(
            PointService pointService,
            PointNotificationSender pointNotificationSender,
            PointMapper pointMapper,
            GpsDeviceAccessControlService accessControlService
    ) {
        this.pointService = pointService;
        this.pointNotificationSender = pointNotificationSender;
        this.pointMapper = pointMapper;
        this.accessControlService = accessControlService;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOG.info("User '{}' established connection", getUsername(session));
    }

    private String getUsername(Session session) {
        var user = session.getUserPrincipal();
        return user == null ? "nobody" : user.getName();
    }

    @OnClose
    public void onClose(Session session) {
        LOG.info("User '{}' closed connection", getUsername(session));
        pointNotificationSender.unsubscribeAll(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.info("User '{}' encountered an error", getUsername(session), throwable);
    }

    @OnMessage
    public void onMessage(Session session, @NotBlank String requestString) throws InvalidInputException {
        LOG.info("Received a message '{}' from the user '{}'", requestString, getUsername(session));
        var result = new LastPointsModel();
        if (PING_MESSAGE.equals(requestString)) {
            // If ping message receive, response with empty result, just for client to know, connection is still OK
            result.setPoints(List.of());
            session.getAsyncRemote().sendObject(result);
            return;
        }
        var pointsRequest = parseRequestString(requestString);
        accessControlService.checkUserAccess(session.getUserPrincipal(), pointsRequest.gpsDeviceId());
        result.setPoints(pointService.getLastPoints(pointsRequest.gpsDeviceId(), pointsRequest.quantity())
                .stream()
                .map(pointMapper::mapToPointModel)
                .toList());
        session.getAsyncRemote().sendObject(result);
        pointNotificationSender.subscribe(pointsRequest.gpsDeviceId(), session);
    }

    private PointsRequest parseRequestString(String requestString) throws InvalidInputException {
        int separatorIndex = requestString.indexOf(':');
        if (separatorIndex < 1 || separatorIndex + 1 == requestString.length()) {
            throw new InvalidInputException(
                    "The request string should contain two parts: GPS device id and number of points");
        }
        long gpsId;
        try {
            gpsId = Long.parseLong(requestString, 0, separatorIndex, 10);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Unable to parse GPS device id from request: " + requestString, e);
        }
        int quantity;
        try {
            quantity = Integer.parseUnsignedInt(requestString, separatorIndex + 1, requestString.length(), 10);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Unable to parse quantity from request: " + requestString, e);
        }
        return new PointsRequest(gpsId, quantity);
    }

    record PointsRequest(long gpsDeviceId, int quantity) { }

}
