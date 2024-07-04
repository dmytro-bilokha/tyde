package com.dmytrobilokha.tyde.point.persistence;

import com.dmytrobilokha.tyde.infra.db.SelectQuery;

import javax.annotation.CheckForNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindGpsDeviceByTokenQuery implements SelectQuery<GpsDevice> {

    private static final String QUERY = "SELECT gd.id, gd.submission_token, gd.description"
            + " FROM gps_device gd"
            + " WHERE gd.submission_token = ?";

    private final String submissionToken;

    public FindGpsDeviceByTokenQuery(String submissionToken) {
        this.submissionToken = submissionToken;
    }

    @CheckForNull
    @Override
    public GpsDevice mapResultSet(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return new GpsDevice(
                resultSet.getLong("id"),
                resultSet.getString("submission_token"),
                resultSet.getString("description")
        );
    }

    @Override
    public String getQueryString() {
        return QUERY;
    }

    @Override
    public void setParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1, submissionToken);
    }

}
