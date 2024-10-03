package com.dmytrobilokha.tyde.infra.jaxrs;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ExceptionToResponseMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        return ExceptionMappingUtil.mapToResponse(exception);
    }

}
