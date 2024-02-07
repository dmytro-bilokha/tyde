package com.dmytrobilokha.tyde.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.transaction.Transactional;

import javax.annotation.CheckForNull;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

@ApplicationScoped
@Transactional
public class UserService implements UserServiceMXBean {

    private static final int TOKEN_PART_LENGTH = 80;
    private static final char TOKEN_PARTS_SEPARATOR = ':';
    private static final char[] TOKEN_PART_CHARS =
            "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[a-zA-Z0-9]+:[a-zA-Z0-9]+");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
    public void createUser(String login, String password) {
        var passwordHash = passwordHasher.generate(password.toCharArray());
        userRepository.insertUser(login, passwordHash);
    }

    @CheckForNull
    public User validateUser(String login, char[] password) {
        var user = userRepository.findUserByLogin(login);
        if (user == null) {
            return null;
        }
        if (passwordHasher.verify(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    @CheckForNull
    public User validateUser(String tokenString) {
        var tokenParts = parseTokenString(tokenString);
        if (tokenParts == null) {
            return null;
        }
        var authToken = userRepository.findAuthenticationTokenByLogin(tokenParts.login());
        if (authToken == null) {
            return null;
        }
        if (authToken.validTo().isBefore(LocalDateTime.now())) {
            userRepository.deleteAuthenticationToken(authToken.login());
            return null;
        }
        if (!passwordHasher.verify(tokenParts.password(), authToken.passwordHash())) {
            return null;
        }
        return userRepository.findUserById(authToken.userId());
    }

    @CheckForNull
    public TokenParts parseTokenString(String token) {
        if (!TOKEN_PATTERN.matcher(token).matches()) {
            return null;
        }
        var separatorIndex = token.indexOf(TOKEN_PARTS_SEPARATOR);
        var tokenLoginPart = token.substring(0, separatorIndex);
        var tokenPassword = token.substring(separatorIndex + 1).toCharArray();
        return new TokenParts(tokenLoginPart, tokenPassword);
    }

    public String createToken(String login) {
        var tokenLoginPart = String.valueOf(generateTokenPart());
        var tokenPasswordPart = generateTokenPart();
        var tokenPasswordHash = passwordHasher.generate(tokenPasswordPart);
        userRepository.insertAuthenticationToken(
                login, tokenLoginPart, tokenPasswordHash, LocalDateTime.now().plusMonths(1));
        return tokenLoginPart + TOKEN_PARTS_SEPARATOR + Arrays.toString(tokenPasswordPart);
    }

    public void removeToken(String tokenString) {
        var tokenParts = parseTokenString(tokenString);
        if (tokenParts == null) {
            return;
        }
        userRepository.deleteAuthenticationToken(tokenParts.login());
    }

    private char[] generateTokenPart() {
        var output = new char[TOKEN_PART_LENGTH];
        for (int i = 0; i < TOKEN_PART_LENGTH; i++) {
            var randomIndex = SECURE_RANDOM.nextInt(TOKEN_PART_CHARS.length);
            output[i] = TOKEN_PART_CHARS[randomIndex];
        }
        return output;
    }

    record TokenParts(String login, char[] password) { }

}
