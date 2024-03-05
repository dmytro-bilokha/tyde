package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.UpsertQuery;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteAuthenticationTokenByLoginQuery implements UpsertQuery {

    private final String login;

    public DeleteAuthenticationTokenByLoginQuery(String login) {
        this.login = login;
    }

    @Override
    public String getQueryString() {
        return "DELETE FROM authentication_token WHERE login = ?";
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, login);
    }

}
