package com.dmytrobilokha.tyde.point.persistence;

import com.dmytrobilokha.tyde.infra.db.DbQueryExecutor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.CheckForNull;
import java.util.List;

@ApplicationScoped
public class GpsDeviceRepository {

    private DbQueryExecutor queryExecutor;

    public GpsDeviceRepository() {
        // CDI
    }

    @Inject
    public GpsDeviceRepository(DbQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public List<GpsDevice> findUserReadableDevices(long userId) {
        return queryExecutor.selectList(new FindUserReadableGpsDevicesQuery(userId));
    }

    @CheckForNull
    public GpsDevice findDeviceByToken(String submissionToken) {
        return queryExecutor.selectObject(new FindGpsDeviceByTokenQuery(submissionToken));
    }

}
