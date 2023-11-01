package com.saksham.service.impl;

import com.saksham.domain.RideStatus;
import com.saksham.exception.DriverException;
import com.saksham.exception.RideException;
import com.saksham.model.Driver;
import com.saksham.model.Ride;
import com.saksham.model.User;
import com.saksham.repository.DriverRepository;
import com.saksham.repository.RideRepository;
import com.saksham.request.RideRequest;
import com.saksham.service.Calculators;
import com.saksham.service.DriverService;
import com.saksham.service.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class RideServiceImplementation implements RideService {

    @Autowired
    private DriverService driverService;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private Calculators calculaters;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    public Ride requestRide(RideRequest rideRequest, User user) throws DriverException {

        double pickupLatitude=rideRequest.getPickupLatitude();
        double pickupLongitude=rideRequest.getPickupLongitude();
        double destinationLatitude=rideRequest.getDestinationLatitude();
        double destinationLongitude=rideRequest.getDestinationLongitude();
        String pickupArea=rideRequest.getPickupArea();
        String destinationArea=rideRequest.getDestinationArea();

        Ride existingRide = new Ride();

        List<Driver> availableDrivers=driverService.getAvailableDrivers(pickupLatitude,
                pickupLongitude, 5, existingRide);

        Driver nearestDriver=driverService.findNearestDriver(availableDrivers, pickupLatitude, pickupLongitude);

        if(nearestDriver==null) {
            throw new DriverException("Driver not available");
        }

        Ride ride = createRideRequest(user, nearestDriver,
                pickupLatitude, pickupLongitude,
                destinationLatitude, destinationLongitude,
                pickupArea,destinationArea
        );

        return ride;
    }

    @Override
    public Ride createRideRequest(User user, Driver nearesDriver, double pickupLatitude, double pickupLongitude, double destinationLatitude, double destinationLongitude, String pickupArea, String destinationArea) {
        Ride ride=new Ride();

        ride.setDriver(nearesDriver);
        ride.setUser(user);
        ride.setPickupLatitude(pickupLatitude);
        ride.setPickupLongitude(pickupLongitude);
        ride.setDestinationLatitude(destinationLatitude);
        ride.setDestinationLongitude(destinationLongitude);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPickupArea(pickupArea);
        ride.setDestinationArea(destinationArea);

        return rideRepository.save(ride);
    }

    @Override
    public void acceptRide(Integer rideId) throws RideException {
        Ride ride=findRideById(rideId);

        ride.setStatus(RideStatus.ACCEPTED);

        Driver driver = ride.getDriver();

        driver.setCurrentRide(ride);

        Random random = new Random();

        int otp = random.nextInt(9000) + 1000;
        ride.setOtp(otp);

        driverRepository.save(driver);

        rideRepository.save(ride);
    }

    @Override
    public void declineRide(Integer rideId, Integer driverId) throws RideException {
        Ride ride =findRideById(rideId);
        ride.getDeclinedDrivers().add(driverId);
        List<Driver> availableDrivers=driverService.getAvailableDrivers(ride.getPickupLatitude(),
                ride.getPickupLongitude(), 5,ride);
        Driver nearestDriver=driverService.findNearestDriver(availableDrivers, ride.getPickupLatitude(),
                ride.getPickupLongitude());
        ride.setDriver(nearestDriver);
        rideRepository.save(ride);
    }

    @Override
    public void startRide(Integer rideId, int otp) throws RideException {
        Ride ride=findRideById(rideId);

        if(otp!=ride.getOtp()) {
            throw new RideException("please provide a valid otp");
        }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartTime(LocalDateTime.now());
        rideRepository.save(ride);
    }

    @Override
    public void completeRide(Integer rideId) throws RideException {
        Ride ride=findRideById(rideId);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());;

        double distence=calculaters.calculateDistance(ride.getDestinationLatitude(), ride.getDestinationLongitude(), ride.getPickupLatitude(), ride.getPickupLongitude());

        LocalDateTime start=ride.getStartTime();
        LocalDateTime end=ride.getEndTime();
        Duration duration = Duration.between(start, end);
        long milliSecond = duration.toMillis();

        double fare=calculaters.calculateFare(distence);

        ride.setDistence(Math.round(distence * 100.0) / 100.0);
        ride.setFare((int) Math.round(fare));
        ride.setDuration(milliSecond);
        ride.setEndTime(LocalDateTime.now());


        Driver driver =ride.getDriver();
        driver.getRides().add(ride);
        driver.setCurrentRide(null);

        Integer driversRevenue=(int) (driver.getTotalRevenue()+ Math.round(fare*0.8)) ;
        driver.setTotalRevenue(driversRevenue);

        driverRepository.save(driver);
        rideRepository.save(ride);

    }

    @Override
    public void cancleRide(Integer rideId) throws RideException {
        Ride ride=findRideById(rideId);
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
    }

    @Override
    public Ride findRideById(Integer rideId) throws RideException {
        Optional<Ride> ride=rideRepository.findById(rideId);

        if(ride.isPresent()) {
            return ride.get();
        }
        throw new RideException("ride not exist with id "+rideId);
    }
}
