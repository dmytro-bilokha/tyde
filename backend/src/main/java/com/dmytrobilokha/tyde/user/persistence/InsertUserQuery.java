package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.UpsertQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUserQuery implements UpsertQuery {

    private static final String QUERY = "INSERT INTO app_user (login, password_hash) VALUES (?, ?)";

    private final String login;
    private final String passwordHash;

    public InsertUserQuery(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getQueryString() {
        return QUERY;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, login);
        statement.setString(2, passwordHash);
    }

}
