package com.saksham.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    @NotBlank(message= "Email is Required")
    @Email(message = "Email should be valid")
    private String email;


    private String fullName;

    private String password;

    private String mobile;
}
