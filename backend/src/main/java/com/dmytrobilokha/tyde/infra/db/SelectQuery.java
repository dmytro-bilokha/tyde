package com.dmytrobilokha.tyde.infra.db;

import javax.annotation.CheckForNull;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SelectQuery<T> extends UpsertQuery {

    T mapResultSet(ResultSet resultSet) throws SQLException;

}
