package com.dmytrobilokha.tyde.user.rest;

import com.dmytrobilokha.tyde.infra.exception.InternalApplicationException;
import com.dmytrobilokha.tyde.infra.rest.ExceptionResponse;
import com.dmytrobilokha.tyde.user.AuthenticationConstants;
import com.dmytrobilokha.tyde.user.service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@RequestScoped
@Path("auth")
public class AuthenticationResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

    private SecurityContext securityContext;
    private UserService userService;

    public AuthenticationResource() {
        // CDI requirement
    }

    @Inject
    public AuthenticationResource(SecurityContext securityContext, UserService userService) {
        this.securityContext = securityContext;
        this.userService = userService;
    }

    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpServletRequest req,
                          @Context HttpServletResponse resp,
                          @Valid LoginRequest loginRequest) throws InternalApplicationException {
        var authenticationStatus = securityContext.authenticate(req, resp, AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(loginRequest.getLogin(), loginRequest.getPassword()))
                .rememberMe(loginRequest.isRememberMe())
                .newAuthentication(true)
        );
        switch (authenticationStatus) {
            case NOT_DONE, SEND_FAILURE, SEND_CONTINUE -> {
                LOG.warn("Failed to login using {}", loginRequest);
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ExceptionResponse("Login/password pair is wrong"))
                        .build();
            }
            case SUCCESS -> {
                return Response.ok(new UserDataResponse(loginRequest.getLogin())).build();
            }
        }
        throw new InternalApplicationException("Unexpected authentication status: " + authenticationStatus);
    }

    @POST
    @Path("logout")
    public Response logout(@Context HttpServletRequest req) throws InternalApplicationException {
        var rememberMeToken = Arrays.stream(req.getCookies())
                .filter(cookie -> AuthenticationConstants.REMEMBER_ME_COOKIE_NAME.equals(cookie.getName()))
                .findAny()
                .map(Cookie::getValue)
                .orElse(null);
        // For some weird reason TomEE erases "remember me" cookie value early and passes null instead of its value to
        // RememberMyIdentityStore.removeLoginToken(). That is why I have to remove it here explicitly.
        if (rememberMeToken != null) {
            userService.removeToken(rememberMeToken);
        }
        try {
            req.logout();
        } catch (ServletException e) {
            throw new InternalApplicationException("Failed to logout", e);
        }
        return Response.ok().build();
    }

}
