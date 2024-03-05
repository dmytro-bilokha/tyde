package com.dmytrobilokha.tyde.user.persistence;

import java.util.Set;

public class User {

    private final long id;
    private final String login;
    private final String passwordHash;
    private final Set<String> roles;

    public User(long id, String login, String passwordHash, Set<String> roles) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.roles = Set.copyOf(roles);
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<String> getRoles() {
        return roles;
    }

}
