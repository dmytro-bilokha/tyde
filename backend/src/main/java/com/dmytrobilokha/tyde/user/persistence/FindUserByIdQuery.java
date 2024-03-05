package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.SelectQuery;

import javax.annotation.CheckForNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class FindUserByIdQuery implements SelectQuery<User> {

    private static final String QUERY = "SELECT au.id, au.login, au.password_hash, ar.name FROM app_user au"
            + " LEFT JOIN app_user_role aur ON aur.app_user_id = au.id"
            + " LEFT JOIN app_role ar ON ar.id = aur.app_role_id"
            + " WHERE au.id = ?";

    private final long id;

    public FindUserByIdQuery(long id) {
        this.id = id;
    }

    @CheckForNull
    @Override
    public User mapResultSet(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        var login = resultSet.getString("login");
        var passwordHash = resultSet.getString("password_hash");
        var roles = new HashSet<String>();
        addRoleName(resultSet, roles);
        while (resultSet.next()) {
            addRoleName(resultSet, roles);
        }
        return new User(id, login, passwordHash, roles);
    }

    private void addRoleName(ResultSet resultSet, Set<String> roles) throws SQLException {
        var roleName = resultSet.getString("name");
        if (roleName != null) {
            roles.add(roleName);
        }
    }

    @Override
    public String getQueryString() {
        return QUERY;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setLong(1, id);
    }

}
