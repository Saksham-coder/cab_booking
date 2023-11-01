package com.saksham.service;

import java.util.List;

import com.saksham.exception.DriverException;
import com.saksham.model.Driver;
import com.saksham.model.Ride;
import com.saksham.request.DriverSignUpRequest;
import org.springframework.stereotype.Service;

public interface DriverService {

    public Driver registerDriver(DriverSignUpRequest driverSignupRequest);

    public List<Driver> getAvailableDrivers(double pickupLatitude,
                                            double picupLongitude,double radius, Ride ride);

    public Driver findNearestDriver(List<Driver> availableDrivers,
                                    double picupLatitude, double picupLongitude);

    public Driver getReqDriverProfile(String jwt) throws DriverException;

    public Ride getDriversCurrentRide(Integer driverId) throws DriverException;

    public List<Ride> getAllocatedRides(Integer driverId) throws DriverException;

    public Driver findDriverById(Integer driverId) throws DriverException;

    public List<Ride> completedRides(Integer driverId) throws DriverException;

}
