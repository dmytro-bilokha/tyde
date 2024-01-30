package com.dmytrobilokha.tyde.user;

import com.dmytrobilokha.tyde.infra.db.SelectQuery;

import javax.annotation.CheckForNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FindAuthenticationTokenByLoginQuery implements SelectQuery<AuthenticationToken> {

    private static final String QUERY = "SELECT id, app_user_id, login, password_hash, valid_to"
        + " FROM authentication_token WHERE login = ?";

    private final String login;

    public FindAuthenticationTokenByLoginQuery(String login) {
        this.login = login;
    }

    @CheckForNull
    @Override
    public AuthenticationToken mapResultSet(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return new AuthenticationToken(
                resultSet.getLong("id"),
                resultSet.getLong("app_user_id"),
                resultSet.getString("login"),
                resultSet.getString("password_hash"),
                resultSet.getTimestamp("valid_to").toLocalDateTime()
        );
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
