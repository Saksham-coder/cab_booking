package com.saksham.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saksham.domain.RideStatus;
import jakarta.persistence.*;
//import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Driver driver;

    @JsonIgnore
    private List<Integer> declinedDrivers = new ArrayList<>();

    private double pickupLatitude;

    private double pickupLongitude;

    private double destinationLatitude;

    private double destinationLongitude;

    private String pickupArea;

    private String destinationArea;

    private double distence;

    private long duration;

    private RideStatus status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private double fare;

    private int otp;
    @Embedded
    private PaymentDetails paymentDetails= new PaymentDetails();
}
