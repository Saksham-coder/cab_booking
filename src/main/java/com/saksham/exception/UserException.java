package com.saksham.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserException extends Exception {
    public UserException(String message) {
        super(message);
    }
}
