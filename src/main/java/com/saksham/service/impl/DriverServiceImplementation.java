package com.saksham.service.impl;

import com.saksham.config.JwtUtil;
import com.saksham.domain.RideStatus;
import com.saksham.domain.UserRole;
import com.saksham.exception.DriverException;
import com.saksham.model.Driver;
import com.saksham.model.License;
import com.saksham.model.Ride;
import com.saksham.model.Vehicle;
import com.saksham.repository.DriverRepository;
import com.saksham.repository.LicenseRepository;
import com.saksham.repository.RideRepository;
import com.saksham.repository.VehicleRepository;
import com.saksham.request.DriverSignUpRequest;
import com.saksham.service.Calculators;
import com.saksham.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DriverServiceImplementation implements DriverService {

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    RideRepository rideRepository;

    @Autowired
    Calculators calculators;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Override
    public Driver registerDriver(DriverSignUpRequest driverSignupRequest) {
        License license=driverSignupRequest.getLicense();
        Vehicle vehicle=driverSignupRequest.getVehicle();

        License createdLicense=new License();

        createdLicense.setLicenseState(license.getLicenseState());
        createdLicense.setLicenseNumber(license.getLicenseNumber());
        createdLicense.setLicenseExpirationDate(license.getLicenseExpirationDate());
        createdLicense.setId(license.getId());

//        below must be in rollback transaction
        License savedLicense=licenseRepository.save(createdLicense);

        Vehicle createdVehicle = new Vehicle();

        createdVehicle.setCapacity(vehicle.getCapacity());
        createdVehicle.setColor(vehicle.getColor());
        createdVehicle.setId(vehicle.getId());
        createdVehicle.setLicensePlate(vehicle.getLicensePlate());
        createdVehicle.setMake(vehicle.getMake());
        createdVehicle.setModel(vehicle.getModel());
        createdVehicle.setYear(vehicle.getYear());

        Vehicle savedVehicle = vehicleRepository.save(createdVehicle);

        Driver driver = new Driver();

        String encodedPassword = passwordEncoder.encode(driverSignupRequest.getPassword());

        driver.setEmail(driverSignupRequest.getEmail());
        driver.setName(driverSignupRequest.getName());
        driver.setMobile(driverSignupRequest.getMobile());
        driver.setPassword(encodedPassword);
        driver.setLicense(savedLicense);
        driver.setVehicle(savedVehicle);
        driver.setRole(UserRole.DRIVER) ;

        driver.setLatitude(driverSignupRequest.getLatitude());
        driver.setLongitude(driverSignupRequest.getLongitude());


        Driver createdDriver = driverRepository.save(driver);

        savedLicense.setDriver(createdDriver);
        savedVehicle.setDriver(createdDriver);

        licenseRepository.save(savedLicense);
        vehicleRepository.save(savedVehicle);

        return createdDriver;

    }

    @Override
    public List<Driver> getAvailableDrivers(double pickupLatitude, double picupLongitude, double radius, Ride ride) {
        List<Driver> allDrivers=driverRepository.findAll();

        List<Driver> availableDriver=new ArrayList<>();


        for(Driver driver:allDrivers) {

            if(driver.getCurrentRide()!=null && driver.getCurrentRide().getStatus()!= RideStatus.COMPLETED
            ) {
                continue;
            }
            if(ride.getDeclinedDrivers().contains(driver.getId())) {
                System.out.println("its containes");
                continue;
            }

            double driverLatitude=driver.getLatitude();
            double driverLongitude=driver.getLongitude();

            double distance= calculators.calculateDistance(driverLatitude,driverLongitude, pickupLatitude, picupLongitude);

//			if(distance<=radius) {
            availableDriver.add(driver);
//			}
        }

        return availableDriver;
    }

    @Override
    public Driver findNearestDriver(List<Driver> availableDrivers, double pickupLatitude, double pickupLongitude) {
        double min=Double.MAX_VALUE;;
        Driver nearestDriver = null;

        for(Driver driver : availableDrivers) {
            double driverLatitude=driver.getLatitude();
            double driverLongitude=driver.getLongitude();

            double distance= calculators.calculateDistance(pickupLatitude, pickupLongitude, driverLatitude,driverLongitude);

            if(min>distance) {
                min=distance;
                nearestDriver=driver;
            }
        }

        return nearestDriver;
    }

    @Override
    public Driver getReqDriverProfile(String jwt) throws DriverException {
        String email=jwtUtil.getEmailFromToken(jwt);
        Driver driver= driverRepository.findByEmail(email);
        if(driver==null) {
            throw new DriverException("driver not exist with email " + email);
        }
        return driver;
    }

    @Override
    public Ride getDriversCurrentRide(Integer driverId) throws DriverException {
        Driver driver = findDriverById(driverId);
        return driver.getCurrentRide();
    }

    @Override
    public List<Ride> getAllocatedRides(Integer driverId) throws DriverException {
        List<Ride> allocatedRides= driverRepository.getAllocatedRides(driverId);
        return allocatedRides;
    }

    @Override
    public Driver findDriverById(Integer driverId) throws DriverException {
        Optional<Driver> opt = driverRepository.findById(driverId);
        if(opt.isPresent()) {
            return opt.get();
        }
        throw new DriverException("driver not exist with id "+driverId);
    }

    @Override
    public List<Ride> completedRides(Integer driverId) throws DriverException {
        List <Ride> completedRides = driverRepository.getCompletedRides(driverId);
        return completedRides;
    }
}
