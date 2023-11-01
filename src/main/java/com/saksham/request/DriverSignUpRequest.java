package com.saksham.request;

import com.saksham.domain.UserRole;
import com.saksham.model.License;
import com.saksham.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverSignUpRequest {
    private String name;
    private String email;
    private String mobile;

    private String password;
    private double latitude;
    private double longitude;
    private License license;
    private Vehicle vehicle;
}
