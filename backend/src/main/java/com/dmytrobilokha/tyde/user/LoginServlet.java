package com.dmytrobilokha.tyde.user;

import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

// TODO: remove this servlet after all testing is done
@WebServlet(AuthenticationConstants.LOGIN_SERVLET_PATH)
public class LoginServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    private SecurityContext securityContext;

    public LoginServlet() { }

    @Inject
    public LoginServlet(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (securityContext.getCallerPrincipal() != null) {
            resp.sendRedirect(req.getContextPath() + AuthenticationConstants.DEFAULT_AFTER_LOGIN_PATH);
        }
        var login = req.getParameter("j_username");
        var password = req.getParameter("j_password");
        if (login == null || password == null) {
            resp.sendRedirect(req.getContextPath() + AuthenticationConstants.LOGIN_ERROR_PAGE_PATH);
        }
        var rememberMe = req.getParameter("j_remember_me") != null;
        var authenticationStatus = securityContext.authenticate(req, resp, AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(login, password))
                .rememberMe(rememberMe)
                .newAuthentication(true)
        );
        var referer = req.getHeader("Referer");
        LOG.info("For login '{}' got '{}' status. The referer is '{}'", login, authenticationStatus, referer);
        switch (authenticationStatus) {
            case NOT_DONE, SEND_FAILURE -> {
                resp.sendRedirect(req.getContextPath() + AuthenticationConstants.LOGIN_ERROR_PAGE_PATH);
            }
            case SEND_CONTINUE -> {
                return;
            }
            case SUCCESS -> {
                if (referer != null) {
                    var refererUrl = new URL(referer);
                    var requestUrl = new URL(req.getRequestURL().toString());
                    if (refererUrl.getHost().equalsIgnoreCase(requestUrl.getHost())
                            && refererUrl.getPath().startsWith(req.getContextPath())) {
                        resp.sendRedirect(referer);
                        return;
                    }
                }
                resp.sendRedirect(req.getContextPath() + AuthenticationConstants.DEFAULT_AFTER_LOGIN_PATH);
            }
        }
    }

}
