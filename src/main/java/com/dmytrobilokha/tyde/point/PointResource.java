package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.math.BigDecimal;
import java.time.Instant;

// TODO: implement proper security with separation of devices and user, token for each device, etc
@RequestScoped
@Path("point")
public class PointResource {

    private static final Logger LOG = LoggerFactory.getLogger(PointResource.class);

    private PointService pointService;

    public PointResource() { }

    @Inject
    public PointResource(PointService pointService) {
        this.pointService = pointService;
    }

    @POST
    public Response registerPoint(
            @BeanParam PointInput pointInput) {
        LOG.info("lat={}, lon={}, timestamp={}, speed={}, altitude={}, direction={}, accuracy={}, provider={}",
                pointInput.getLat(), pointInput.getLon(),
                pointInput.getTimestamp(), pointInput.getSpeed(),
                pointInput.getAltitude(), pointInput.getDirection(),
                pointInput.getAccuracy(), pointInput.getProvider());
        var point = new Point();
        point.setLat(new BigDecimal(pointInput.getLat()));
        point.setLon(new BigDecimal(pointInput.getLon()));
        point.setClientTimestamp(Instant.ofEpochSecond(pointInput.getTimestamp()));
        point.setSpeed(new BigDecimal(pointInput.getSpeed()));
        point.setAltitude(new BigDecimal(pointInput.getAltitude()));
        point.setDirection(new BigDecimal(pointInput.getDirection()));
        point.setAccuracy(new BigDecimal(pointInput.getAccuracy()));
        point.setProvider(pointInput.getProvider());
        point.setServerTimestamp(Instant.now());
        pointService.registerPoint(point);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CheckForNull
    public PointModel getLastPoint() {
        var lastPoint = pointService.getLastPoint();
        if (lastPoint == null) {
            return null;
        }
        var lastPointModel = new PointModel();
        lastPointModel.setLat(lastPoint.getLat());
        lastPointModel.setLon(lastPoint.getLon());
        lastPointModel.setTimestamp(lastPoint.getClientTimestamp());
        lastPointModel.setSpeed(lastPoint.getSpeed());
        lastPointModel.setAltitude(lastPoint.getAltitude());
        lastPointModel.setDirection(lastPoint.getDirection());
        lastPointModel.setAccuracy(lastPoint.getAccuracy());
        return lastPointModel;
    }

}
