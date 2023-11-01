package com.saksham.controller;

import com.saksham.config.JwtUtil;
import com.saksham.domain.UserRole;
import com.saksham.exception.UserException;
import com.saksham.model.Driver;
import com.saksham.model.User;
import com.saksham.repository.DriverRepository;
import com.saksham.repository.UserRepository;
import com.saksham.request.DriverSignUpRequest;
import com.saksham.request.LoginRequest;
import com.saksham.request.SignUpRequest;
import com.saksham.response.JwtResponse;
import com.saksham.service.CustomUserDetail;
import com.saksham.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetail customUserDetail;

    @Autowired
    private DriverService driverService;

    @PostMapping("/user/signup")
    public ResponseEntity<JwtResponse> signupHandler(@RequestBody SignUpRequest signUpRequest) throws UserException {
        User user = userRepository.findByEmail(signUpRequest.getEmail());

        JwtResponse jwtResponse=new JwtResponse();


        if(user!=null) {
            throw new UserException("User Already Exist With Email "+signUpRequest.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        // Create new user account
        User createdUser = new User();
        createdUser.setEmail(signUpRequest.getEmail());
        createdUser.setPassword(encodedPassword);
        createdUser.setFullName(signUpRequest.getFullName());
        createdUser.setMobile(signUpRequest.getMobile());
        createdUser.setRole(UserRole.USER);

        User savedUser=userRepository.save(createdUser);

        Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtil.generateJwtToken(authentication);

        jwtResponse.setJwt(jwt);
        jwtResponse.setAuthenticated(true);
        jwtResponse.setError(false);
        jwtResponse.setErrorDetails(null);
        jwtResponse.setType(UserRole.USER);
        jwtResponse.setMessage("Account Created Successfully: "+savedUser.getFullName());

        return new ResponseEntity<JwtResponse>(jwtResponse,
                HttpStatus.ACCEPTED);
    }

    @PostMapping("/driver/signup")
    public ResponseEntity<JwtResponse> driverSignupHandler(@RequestBody DriverSignUpRequest driverSignupRequest){

        Driver driver = driverRepository.findByEmail(driverSignupRequest.getEmail());

        JwtResponse jwtResponse=new JwtResponse();

        if(driver!=null) {
            jwtResponse.setAuthenticated(false);
            jwtResponse.setErrorDetails("email already used with another account");
            jwtResponse.setError(true);
            return new ResponseEntity<JwtResponse>(jwtResponse,HttpStatus.BAD_REQUEST);
        }

        Driver createdDriver=driverService.registerDriver(driverSignupRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(createdDriver.getEmail(), createdDriver.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateJwtToken(authentication);

        jwtResponse.setJwt(jwt);
        jwtResponse.setAuthenticated(true);
        jwtResponse.setError(false);
        jwtResponse.setErrorDetails(null);
        jwtResponse.setType(UserRole.DRIVER);
        jwtResponse.setMessage("Account Created Successfully: "+createdDriver.getName());

        return new ResponseEntity<JwtResponse>(jwtResponse,HttpStatus.ACCEPTED);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> handleSignIn(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateJwtToken(authentication);

        JwtResponse jwtResponse=new JwtResponse();

        jwtResponse.setJwt(jwt);
        jwtResponse.setAuthenticated(true);
        jwtResponse.setError(false);
        jwtResponse.setErrorDetails(null);
        jwtResponse.setMessage("Account Login Successfully: " + email);

        return new ResponseEntity<JwtResponse>(jwtResponse,HttpStatus.OK);
    }

    public Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetail.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
