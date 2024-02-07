package com.dmytrobilokha.tyde.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

@ApplicationScoped
public class AppIdentityStore implements IdentityStore {

    private UserService userService;

    public AppIdentityStore() {
        // CDI
    }

    @Inject
    public AppIdentityStore(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential passwordCredential) {
            var login = passwordCredential.getCaller();
            var password = passwordCredential.getPassword().getValue();
            var user = userService.validateUser(login, password);
            if (user != null) {
                return new CredentialValidationResult(login, user.getRoles());
            }
        }
        return CredentialValidationResult.INVALID_RESULT;
    }

}
