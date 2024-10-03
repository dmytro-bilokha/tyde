package com.dmytrobilokha.tyde.infra.jaxrs;

import com.dmytrobilokha.tyde.infra.exception.InternalApplicationException;
import com.dmytrobilokha.tyde.infra.exception.InvalidInputException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

final class ExceptionMappingUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionMappingUtil.class);
    private static final Map<Class<? extends Exception>, Response.Status> APP_EXCEPTION_STATUS = Map.of(
            InternalApplicationException.class, Response.Status.INTERNAL_SERVER_ERROR,
            InvalidInputException.class, Response.Status.BAD_REQUEST);

    private ExceptionMappingUtil() {
        // no instance
    }

    static Response mapToResponse(Exception exception) {
        var status = APP_EXCEPTION_STATUS.get(exception.getClass());
        if (status != null) {
            return logAndConvert(exception, status, exception.getMessage());
        }
        for (Map.Entry<Class<? extends Exception>, Response.Status> entry : APP_EXCEPTION_STATUS.entrySet()) {
            if (entry.getKey().isAssignableFrom(exception.getClass())) {
                return logAndConvert(exception, entry.getValue(), exception.getMessage());
            }
        }
        return logAndConvert(exception, Response.Status.INTERNAL_SERVER_ERROR, "Unknown internal exception");
    }

    private static Response logAndConvert(Exception e, Response.Status status, String message) {
        LOG.error("Exception thrown in JAX-RS resource", e);
        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ExceptionResponse(message))
                .build();
    }

}
