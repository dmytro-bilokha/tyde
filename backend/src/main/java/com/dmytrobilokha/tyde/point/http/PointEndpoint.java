package com.dmytrobilokha.tyde.point.http;

import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import com.dmytrobilokha.tyde.infra.websocket.JsonEncoder;
import com.dmytrobilokha.tyde.point.PointMapper;
import com.dmytrobilokha.tyde.point.service.GpsDeviceAccessControlService;
import com.dmytrobilokha.tyde.point.service.PointService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ServerEndpoint(value = "/point-endpoint", encoders = {JsonEncoder.class}, decoders = {PointsRequestDecoder.class})
public class PointEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(PointEndpoint.class);

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
        LOG.info("Closing connection for '{}'", getUsername(session));
        pointNotificationSender.unsubscribeAll(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOG.info("User '{}' encountered an error", getUsername(session), throwable);
    }

    @OnMessage
    public void onMessage(Session session, @NotNull @Valid PointsRequest pointsRequest) throws InvalidInputException {
        LOG.info("Received a message '{}' from the user '{}'", pointsRequest, getUsername(session));
        var result = new LastPointsModel();
        accessControlService.checkUserAccess(session.getUserPrincipal(), pointsRequest.getGpsDeviceId());
        result.setPoints(pointService.getLastPoints(
                        pointsRequest.getGpsDeviceId(),
                        pointsRequest.getLastPointId(),
                        pointsRequest.getQuantity())
                .stream()
                .map(pointMapper::mapToPointModel)
                .toList());
        session.getAsyncRemote().sendObject(result);
        pointNotificationSender.subscribe(pointsRequest.getGpsDeviceId(), session);
    }

}
