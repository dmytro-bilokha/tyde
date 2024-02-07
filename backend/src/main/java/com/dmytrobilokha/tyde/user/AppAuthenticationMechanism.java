package com.dmytrobilokha.tyde.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AutoApplySession;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.authentication.mechanism.http.RememberMe;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@AutoApplySession
@RememberMe(
        cookieName = AuthenticationConstants.REMEMBER_ME_COOKIE_NAME,
        cookieSecureOnly = false,
        cookieMaxAgeSeconds = AuthenticationConstants.REMEMBER_ME_COOKIE_LIFE)
@LoginToContinue(
        loginPage = AuthenticationConstants.LOGIN_PAGE_PATH,
        errorPage = AuthenticationConstants.LOGIN_ERROR_PAGE_PATH)
@ApplicationScoped
public class AppAuthenticationMechanism implements HttpAuthenticationMechanism {

    private IdentityStoreHandler identityStoreHandler;

    public AppAuthenticationMechanism() {
        // CDI
    }

    @Inject
    public AppAuthenticationMechanism(IdentityStoreHandler identityStoreHandler) {
        this.identityStoreHandler = identityStoreHandler;
    }

    @Override
    public AuthenticationStatus validateRequest(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageContext context
    ) throws AuthenticationException {
        var credential = context.getAuthParameters().getCredential();
        if (credential != null) {
            return context.notifyContainerAboutLogin(identityStoreHandler.validate(credential));
        }
        return context.doNothing();
    }

    @Override
    public void cleanSubject(
            HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) {
        HttpAuthenticationMechanism.super.cleanSubject(request, response, httpMessageContext);
    }

}
