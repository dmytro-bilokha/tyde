package com.dmytrobilokha.tyde.point;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Path("point")
public class PointResource {

    private static final Logger LOG = LoggerFactory.getLogger(PointResource.class);

    @POST
    public Response registerPoint(
            @FormParam("lat") String lat,
            @FormParam("lon") String lon,
            @FormParam("timestamp") String timestamp,
            @FormParam("speed") String speed,
            @FormParam("altitude") String altitude,
            @FormParam("direction") String direction,
            @FormParam("accuracy") String accuracy,
            @FormParam("provider") String provider) {
        LOG.info("lat={}, lon={}, timestamp={}, speed={}, altitude={}, direction={}, accuracy={}, provider={}",
                lat, lon, timestamp, speed, altitude, direction, accuracy, provider);
        return Response.ok().build();
    }

}
