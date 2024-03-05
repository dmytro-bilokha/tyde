package com.dmytrobilokha.tyde.user.authmechanism;

import com.dmytrobilokha.tyde.user.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.CallerPrincipal;
import jakarta.security.enterprise.credential.RememberMeCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.RememberMeIdentityStore;

import javax.annotation.CheckForNull;
import java.util.Set;

@ApplicationScoped
public class AppRememberMeIdentityStore implements RememberMeIdentityStore {

    private UserService userService;

    public AppRememberMeIdentityStore() {
        // CDI
    }

    @Inject
    public AppRememberMeIdentityStore(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CredentialValidationResult validate(RememberMeCredential credential) {
        var tokenString = credential.getToken();
        var user = userService.validateUser(tokenString);
        if (user == null) {
            return CredentialValidationResult.INVALID_RESULT;
        }
        return new CredentialValidationResult(user.getLogin(), user.getRoles());
    }

    @Override
    public String generateLoginToken(CallerPrincipal callerPrincipal, Set<String> groups) {
        return userService.createToken(callerPrincipal.getName());
    }

    @Override
    public void removeLoginToken(@CheckForNull String token) {
        if (token == null) {
            return;
        }
        userService.removeToken(token);
    }

}
