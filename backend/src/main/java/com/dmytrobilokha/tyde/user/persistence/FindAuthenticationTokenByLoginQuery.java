package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.SelectQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindAuthenticationTokenByLoginQuery implements SelectQuery<AuthenticationToken> {

    private static final String QUERY = "SELECT id, app_user_id, login, password_hash, valid_to"
        + " FROM authentication_token WHERE login = ?";

    private final String login;

    public FindAuthenticationTokenByLoginQuery(String login) {
        this.login = login;
    }

    @Override
    public AuthenticationToken mapResultSet(ResultSet resultSet) throws SQLException {
        var result = new AuthenticationToken(
                resultSet.getLong("id"),
                resultSet.getLong("app_user_id"),
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                resultSet.getTimestamp("valid_to").toLocalDateTime()
        );
        resultSet.next();
        return result;
    }

    @Override
    public String getQueryString() {
        return QUERY;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, login);
    }

}
