package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.UpsertQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class InsertAuthenticationTokenQuery implements UpsertQuery {

    private static final String QUERY = "INSERT INTO authentication_token (app_user_id, login, password_hash, valid_to)"
            + " SELECT au.id, ?, ?, ? FROM app_user au WHERE au.login = ?";

    private final String userLogin;
    private final String login;
    private final String passwordHash;
    private final LocalDateTime validTo;

    public InsertAuthenticationTokenQuery(String userLogin, String login, String passwordHash, LocalDateTime validTo) {
        this.userLogin = userLogin;
        this.login = login;
        this.passwordHash = passwordHash;
        this.validTo = validTo;
    }

    @Override
    public String getQueryString() {
        return QUERY;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, login);
        statement.setString(2, passwordHash);
        statement.setTimestamp(3, Timestamp.valueOf(validTo));
        statement.setString(4, userLogin);
    }

}
