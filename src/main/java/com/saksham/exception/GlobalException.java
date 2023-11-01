package com.saksham.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetails> userExceptionHandler(UserException ue, WebRequest req){

        ErrorDetails err=new ErrorDetails(
                ue.getMessage(),req.getDescription(false), LocalDateTime.now()
        );


        return new ResponseEntity<ErrorDetails>(err, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(DriverException.class)
    public ResponseEntity<ErrorDetails> driverrExceptionHandler(DriverException ue, WebRequest req){

        ErrorDetails err=new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

        return new ResponseEntity<ErrorDetails>(err,HttpStatus.ACCEPTED);

    }
    @ExceptionHandler(RideException.class)
    public ResponseEntity<ErrorDetails> RideExceptionHandler(RideException ue, WebRequest req){

        ErrorDetails err=new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

        return new ResponseEntity<ErrorDetails>(err,HttpStatus.ACCEPTED);

    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDetails> handleValidationException(ConstraintViolationException ex) {
        StringBuilder errorMessage = new StringBuilder();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorMessage.append(violation.getMessage() + "\n");
        }
        ErrorDetails err= new ErrorDetails(errorMessage.toString(),"Validation Error", LocalDateTime.now());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException me){
        ErrorDetails err=new ErrorDetails(me.getBindingResult().getFieldError().getDefaultMessage(),"validation error",LocalDateTime.now());
        return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> otherExceptionHandler(Exception ue, WebRequest req){

        ErrorDetails err=new ErrorDetails(ue.getMessage(),req.getDescription(false), LocalDateTime.now());

        return new ResponseEntity<ErrorDetails>(err,HttpStatus.ACCEPTED);

    }
}
