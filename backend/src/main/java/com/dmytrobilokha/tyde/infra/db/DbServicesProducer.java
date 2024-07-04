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

    // TODO: inject query executor instead of data source, repositories should not use data source
    @UserDataSource
    @Produces
    DataSource produceUserDbDataSource() {
        return userDataSource;
    }

    @Produces
    DataSource produceTydeDbDataSource() {
        return tydeDataSource;
    }

}
