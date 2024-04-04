package com.dmytrobilokha.tyde.user.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;
import java.util.Optional;

// TODO: add security constraints
@RequestScoped
@Path("user")
public class UserResource {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserDataResponse getUserData(@Context SecurityContext securityContext) {
        var login = Optional.ofNullable(securityContext.getUserPrincipal())
                .map(Principal::getName)
                .orElseThrow(() -> new IllegalStateException("This resource should be secured"));
        return new UserDataResponse(login);
    }

}
