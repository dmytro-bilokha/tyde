package com.dmytrobilokha.tyde.infra;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import jakarta.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;

@DatabaseIdentityStoreDefinition(
        dataSourceLookup = "java:comp/env/jdbc/AppUserDB",
        callerQuery = "SELECT password_hash FROM app_user WHERE login = ?",
        groupsQuery = "SELECT ar.name FROM app_role ar INNER JOIN app_user_role aur ON ar.id = aur.app_role_id"
            + " INNER JOIN app_user au ON aur.app_user_id = au.id WHERE au.login = ?",
        hashAlgorithm = Pbkdf2PasswordHash.class,
        hashAlgorithmParameters = {
                "Pbkdf2PasswordHash.Algorithm=PBKDF2WithHmacSHA512",
                "Pbkdf2PasswordHash.Iterations=350000",
                "Pbkdf2PasswordHash.SaltSizeBytes=32",
                "Pbkdf2PasswordHash.KeySizeBytes=32"
        }
)
@BasicAuthenticationMechanismDefinition(realmName = "tyde")
@ApplicationScoped
public class AppConfig {
}
