package com.dmytrobilokha.tyde.user;

import com.dmytrobilokha.tyde.infra.db.DbException;
import com.dmytrobilokha.tyde.infra.db.DbQueryExecutor;
import com.dmytrobilokha.tyde.infra.db.SelectQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.CheckForNull;
import javax.sql.DataSource;

@ApplicationScoped
public class UserRepository {

    private DbQueryExecutor queryExecutor;

    public UserRepository() {
        // CDI
    }

    @Inject
    public UserRepository(DataSource userDataSource) {
        this.queryExecutor = new DbQueryExecutor(userDataSource);
    }

    @CheckForNull
    public Long insertUser(String login, String passwordHash) throws DbException {
        return queryExecutor.insert(new InsertUserQuery(login, passwordHash));
    }

    @CheckForNull
    public User findUserByLogin(String login) throws DbException {
        return queryExecutor.selectObject(new FindUserByLoginQuery(login));
    }

}
