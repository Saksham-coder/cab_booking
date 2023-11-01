package com.saksham.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saksham.domain.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String email;
    private String mobile;
    private double ratig;
    private double latitude;
    private double longitude;

    private UserRole role;

    private String password;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL)
    private License license;

    @JsonIgnore
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Ride> rides;

    @OneToOne(mappedBy="driver",cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle vehicle;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    private Ride currentRide;

    private Integer totalRevenue=0;

}
