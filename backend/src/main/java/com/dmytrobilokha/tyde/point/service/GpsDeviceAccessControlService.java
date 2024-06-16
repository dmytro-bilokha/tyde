package com.dmytrobilokha.tyde.point.service;

import com.dmytrobilokha.tyde.point.persistence.GpsDevice;
import com.dmytrobilokha.tyde.point.persistence.GpsDeviceRepository;
import com.dmytrobilokha.tyde.user.persistence.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.CheckForNull;
import java.security.Principal;
import java.util.List;

@ApplicationScoped
public class GpsDeviceAccessControlService {

    private GpsDeviceRepository gpsDeviceRepository;
    private UserRepository userRepository;

    public GpsDeviceAccessControlService() {
        // CDI
    }

    @Inject
    public GpsDeviceAccessControlService(GpsDeviceRepository gpsDeviceRepository, UserRepository userRepository) {
        this.gpsDeviceRepository = gpsDeviceRepository;
        this.userRepository = userRepository;
    }

    public void checkUserAccess(@CheckForNull Principal principal, long gpsDeviceId) {
        var accessGranted = findUserReadableGpsDevices(principal)
                .stream()
                .anyMatch(gpsDevice -> gpsDevice.id() == gpsDeviceId);
        if (!accessGranted) {
            throw new SecurityException(
                    "User " + principal + " is not allowed to access GPS device with id=" + gpsDeviceId);
        }
    }

    public List<GpsDevice> findUserReadableGpsDevices(@CheckForNull Principal principal) {
        if (principal == null) {
            return List.of();
        }
        var user = userRepository.findUserByLogin(principal.getName());
        if (user == null) {
            throw new IllegalStateException("Unable to find user with login: " + principal.getName());
        }
        return gpsDeviceRepository.findUserReadableDevices(user.getId());
    }

}
