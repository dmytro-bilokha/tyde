package com.dmytrobilokha.tyde.point.persistence;

import com.dmytrobilokha.tyde.infra.db.DbQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.CheckForNull;
import javax.sql.DataSource;
import java.util.List;

@ApplicationScoped
public class GpsDeviceRepository {

    private DbQueryExecutor queryExecutor;

    public GpsDeviceRepository() {
        // CDI
    }

    @Inject
    public GpsDeviceRepository(DataSource pointsDataSource) {
        this.queryExecutor = new DbQueryExecutor(pointsDataSource);
    }

    public List<GpsDevice> findUserReadableDevices(long userId) {
        return queryExecutor.selectList(new FindUserReadableGpsDevicesQuery(userId));
    }

    @CheckForNull
    public GpsDevice findDeviceByToken(String submissionToken) {
        return queryExecutor.selectObject(new FindGpsDeviceByTokenQuery(submissionToken));
    }

}
