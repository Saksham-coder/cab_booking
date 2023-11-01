package com.saksham.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message= "Email is Required")
    @Email(message = "Email should be valid")
    private String email;


    private String fullName;

    private String password;

    private String mobile;
}
