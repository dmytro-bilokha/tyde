package com.dmytrobilokha.tyde.user.persistence;

import com.dmytrobilokha.tyde.infra.db.DbQueryExecutor;
import com.dmytrobilokha.tyde.infra.db.UserDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.CheckForNull;
import java.time.LocalDateTime;

@ApplicationScoped
public class UserRepository {

    private DbQueryExecutor queryExecutor;

    public UserRepository() {
        // CDI
    }

    @Inject
    public UserRepository(@UserDataSource DbQueryExecutor dbQueryExecutor) {
        this.queryExecutor = dbQueryExecutor;
    }

    @CheckForNull
    public Long insertUser(String login, String passwordHash) {
        return queryExecutor.insert(new InsertUserQuery(login, passwordHash));
    }

    @CheckForNull
    public User findUserByLogin(String login) {
        return queryExecutor.selectObject(new FindUserByLoginQuery(login));
    }

    @CheckForNull
    public User findUserById(long id) {
        return queryExecutor.selectObject(new FindUserByIdQuery(id));
    }

    @CheckForNull
    public Long insertAuthenticationToken(String userLogin, String login, String passwordHash, LocalDateTime validTo) {
        return queryExecutor.insert(new InsertAuthenticationTokenQuery(userLogin, login, passwordHash, validTo));
    }

    public int deleteAuthenticationToken(String login) {
        return queryExecutor.delete(new DeleteAuthenticationTokenByLoginQuery(login));
    }

    @CheckForNull
    public AuthenticationToken findAuthenticationTokenByLogin(String login) {
        return queryExecutor.selectObject(new FindAuthenticationTokenByLoginQuery(login));
    }

}
