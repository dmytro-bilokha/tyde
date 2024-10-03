package com.dmytrobilokha.tyde.point.jaxrs;

import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import com.dmytrobilokha.tyde.point.PointMapper;
import com.dmytrobilokha.tyde.point.service.PointService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Path("point")
public class PointResource {

    private static final Logger LOG = LoggerFactory.getLogger(PointResource.class);

    private PointMapper pointMapper;
    private PointService pointService;

    public PointResource() { }

    @Inject
    public PointResource(PointMapper pointMapper,
                         PointService pointService) {
        this.pointMapper = pointMapper;
        this.pointService = pointService;
    }

    @POST
    public Response registerPoint(@Valid @BeanParam PointInput pointInput) throws InvalidInputException {
        LOG.info(
                "token={}, lat={}, lon={}, timestamp={}, speed={}, altitude={}, direction={}, accuracy={}, provider={}",
                pointInput.getToken(),
                pointInput.getLat(), pointInput.getLon(),
                pointInput.getTimestamp(), pointInput.getSpeed(),
                pointInput.getAltitude(), pointInput.getDirection(),
                pointInput.getAccuracy(), pointInput.getProvider());
        pointService.registerPoint(pointMapper.mapToPoint(pointInput));
        return Response.ok().build();
    }

}
