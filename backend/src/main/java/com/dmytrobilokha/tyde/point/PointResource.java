package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Response registerPoint(@Valid @BeanParam PointInput pointInput) {
        LOG.info("lat={}, lon={}, timestamp={}, speed={}, altitude={}, direction={}, accuracy={}, provider={}",
                pointInput.getLat(), pointInput.getLon(),
                pointInput.getTimestamp(), pointInput.getSpeed(),
                pointInput.getAltitude(), pointInput.getDirection(),
                pointInput.getAccuracy(), pointInput.getProvider());
        var point = new Point();
        point.setLat(pointInput.getLat());
        point.setLon(pointInput.getLon());
        point.setClientTimestamp(Instant.ofEpochSecond(pointInput.getTimestamp()));
        point.setSpeed(pointInput.getSpeed());
        point.setAltitude(pointInput.getAltitude());
        point.setDirection(pointInput.getDirection());
        point.setAccuracy(pointInput.getAccuracy());
        point.setProvider(pointInput.getProvider());
        point.setServerTimestamp(Instant.now());
        pointService.registerPoint(point);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LastPointsModel getLastPoints(@Min(1) @Max(100) @QueryParam("quantity") int quantity) {
        var result = new LastPointsModel();
        result.setPoints(pointService.getLastPoints(quantity)
                .stream()
                .map(this::mapPoint)
                .toList());
        return result;
    }

    private PointModel mapPoint(Point point) {
        var pointModel = new PointModel();
        pointModel.setLat(point.getLat());
        pointModel.setLon(point.getLon());
        pointModel.setTimestamp(point.getClientTimestamp());
        pointModel.setSpeed(point.getSpeed());
        pointModel.setAltitude(point.getAltitude());
        pointModel.setDirection(point.getDirection());
        pointModel.setAccuracy(point.getAccuracy());
        return pointModel;
    }

}
