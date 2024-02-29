package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        pointService.registerPoint(MappingUtil.toPoint(pointInput));
        return Response.ok().build();
    }

}
