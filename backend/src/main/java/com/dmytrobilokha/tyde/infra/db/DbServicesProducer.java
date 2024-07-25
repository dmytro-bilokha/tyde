package com.dmytrobilokha.tyde.infra.db;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import javax.sql.DataSource;

@ApplicationScoped
public class DbServicesProducer {

    @Resource(name = "jdbc/AppUserDB")
    private DataSource userDataSource;
    @Resource(name = "jdbc/TydeDB")
    private DataSource tydeDataSource;

    @UserDataSource
    @Produces
    DbQueryExecutor produceUserDbQueryExecutor() {
        return new DbQueryExecutor(userDataSource);
    }

    @Produces
    DbQueryExecutor produceTydeDbQueryExecutor() {
        return new DbQueryExecutor(tydeDataSource);
    }

}
