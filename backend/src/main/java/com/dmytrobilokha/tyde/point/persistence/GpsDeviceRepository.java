package com.dmytrobilokha.tyde.point.persistence;

import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.CheckForNull;
import java.util.List;

// TODO: implement real persistence
@ApplicationScoped
public class GpsDeviceRepository {

    private static final List<GpsDevice> DEVICES =
        List.of(
                new GpsDevice(1L, "testSub", "test device 1"),
                new GpsDevice(2L, "testSub2", "test device 2")
        );

    // private DbQueryExecutor queryExecutor;

    public GpsDeviceRepository() {
        // CDI
    }

    /*
    @Inject
    public GpsDeviceRepository(DataSource pointsDataSource) {
        this.queryExecutor = new DbQueryExecutor(pointsDataSource);
    }
    */

    public List<GpsDevice> findUserReadableDevices(long userId) {
        return DEVICES;
    }

    @CheckForNull
    public GpsDevice findDeviceByToken(String submissionToken) {
        return DEVICES.stream()
                .filter(gpsDevice -> submissionToken.equals(gpsDevice.submissionToken()))
                .findAny()
                .orElse(null);
    }

}
