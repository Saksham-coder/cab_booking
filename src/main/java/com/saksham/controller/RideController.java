package com.saksham.controller;

import com.saksham.controller.mapper.DtoMapper;
import com.saksham.dto.RideDto;
import com.saksham.exception.DriverException;
import com.saksham.exception.RideException;
import com.saksham.exception.UserException;
import com.saksham.model.Driver;
import com.saksham.model.Ride;
import com.saksham.model.User;
import com.saksham.request.RideRequest;
import com.saksham.request.StartRideRequest;
import com.saksham.response.MessageResponse;
import com.saksham.service.DriverService;
import com.saksham.service.RideService;
import com.saksham.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private UserService userService;

    @Autowired
    private RideService rideService;

    @Autowired
    private DriverService driverService;

    @PostMapping("/request")
    public ResponseEntity<RideDto> userRequestRideHandler(@RequestBody RideRequest rideRequest, @RequestHeader("Authorization") String jwt) throws UserException, DriverException {

        User user =userService.findUserByToken(jwt);

        Ride ride=rideService.requestRide(rideRequest, user);

        RideDto rideDto= DtoMapper.toRideDto(ride);

        return new ResponseEntity<>(rideDto, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{rideId}/accept")
    public ResponseEntity<MessageResponse> acceptRideHandler(@PathVariable Integer rideId) throws UserException, RideException {

        rideService.acceptRide(rideId);

        MessageResponse res=new MessageResponse("Ride Accepted By Driver");

        return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{rideId}/decline")
    public ResponseEntity<MessageResponse> declineRideHandler(@RequestHeader("Authorization") String jwt, @PathVariable Integer rideId)
            throws UserException, RideException, DriverException{

        Driver driver = driverService.getReqDriverProfile(jwt);

        rideService.declineRide(rideId, driver.getId());

        MessageResponse res=new MessageResponse("Ride decline By Driver");

        return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{rideId}/start")
    public ResponseEntity<MessageResponse> rideStartHandler(@PathVariable Integer rideId, @RequestBody StartRideRequest req) throws UserException, RideException{

        rideService.startRide(rideId,req.getOtp());

        MessageResponse res=new MessageResponse("Ride is started");

        return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<MessageResponse> rideCompleteHandler(@PathVariable Integer rideId) throws UserException, RideException{

        rideService.completeRide(rideId);

        MessageResponse res=new MessageResponse("Ride Is Completed Thank You For Booking Cab");

        return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideDto> findRideByIdHandler(@PathVariable Integer rideId, @RequestHeader("Authorization") String jwt) throws UserException, RideException{

        User user =userService.findUserByToken(jwt);

        Ride ride =rideService.findRideById(rideId);


        RideDto rideDto=DtoMapper.toRideDto(ride);


        return new ResponseEntity<RideDto>(rideDto,HttpStatus.ACCEPTED);
    }
}
