package com.dmytrobilokha.tyde.point.persistence;

import com.dmytrobilokha.tyde.infra.db.SelectQuery;

import javax.annotation.CheckForNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindUserReadableGpsDevicesQuery implements SelectQuery<GpsDevice> {

    private static final String QUERY = "SELECT gd.id, gd.submission_token, gd.description"
            + " FROM gps_device gd INNER JOIN gps_device_read_user gdru ON gd.id = gdru.gps_device_id"
            + " WHERE gdru.app_user_id = ? ORDER BY gd.description, gd.id";

    private final long userId;

    public FindUserReadableGpsDevicesQuery(long userId) {
        this.userId = userId;
    }

    @Override
    public GpsDevice mapResultSet(ResultSet resultSet) throws SQLException {
        var result = new GpsDevice(
                resultSet.getLong("id"),
                resultSet.getString("submission_token"),
                resultSet.getString("description")
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
        statement.setLong(1, userId);
    }

}
