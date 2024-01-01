package com.dmytrobilokha.tyde.user;

import com.dmytrobilokha.tyde.infra.db.DbException;
import com.dmytrobilokha.tyde.infra.exception.InternalApplicationException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import java.util.Map;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class UserService implements UserServiceMXBean {

    private Pbkdf2PasswordHash passwordHasher;
    private UserRepository userRepository;
    public UserService() {
        // CDI
    }

    @Inject
    public UserService(Pbkdf2PasswordHash passwordHasher, UserRepository userRepository) {
        this.passwordHasher = passwordHasher;
        passwordHasher.initialize(
                Map.of(
                        "Pbkdf2PasswordHash.Algorithm", "PBKDF2WithHmacSHA512",
                        "Pbkdf2PasswordHash.Iterations", "350000",
                        "Pbkdf2PasswordHash.SaltSizeBytes", "32",
                        "Pbkdf2PasswordHash.KeySizeBytes", "32"
                )
        );
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(String login, String password) throws InternalApplicationException {
        var passwordHash = passwordHasher.generate(password.toCharArray());
        try {
            userRepository.insertUser(login, passwordHash);
        } catch (DbException e) {
            throw new InternalApplicationException("Failed to create a user", e);
        }
    }

}
