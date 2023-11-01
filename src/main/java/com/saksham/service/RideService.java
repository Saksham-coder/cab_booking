package com.saksham.service;

import com.saksham.exception.DriverException;
import com.saksham.exception.RideException;
import com.saksham.model.Driver;
import com.saksham.model.Ride;
import com.saksham.model.User;
import com.saksham.request.RideRequest;

public interface RideService {
    public Ride requestRide(RideRequest rideRequest, User user) throws DriverException;

    public Ride createRideRequest(User user, Driver nearesDriver,
                                  double picupLatitude,double pickupLongitude,
                                  double destinationLatitude,double destinationLongitude,
                                  String pickupArea,String destinationArea);

    public void acceptRide(Integer rideId) throws RideException;

    public void declineRide(Integer rideId, Integer driverId) throws RideException;

    public void startRide(Integer rideId,int opt) throws RideException;

    public void completeRide(Integer rideId) throws RideException;

    public void cancleRide(Integer rideId) throws RideException;

    public Ride findRideById(Integer rideId) throws RideException;
}
