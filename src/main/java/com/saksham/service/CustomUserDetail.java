package com.saksham.service;

import com.saksham.model.Driver;
import com.saksham.model.User;
import com.saksham.repository.DriverRepository;
import com.saksham.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class CustomUserDetail implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        username is coming as email only
        List<GrantedAuthority> authorities = new LinkedList<>();

        User user = userRepository.findByEmail(username);

        if(user!=null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
        }

        Driver driver = driverRepository.findByEmail(username);

        if(driver!=null) {
            return new org.springframework.security.core.userdetails.User(driver.getEmail(), driver.getPassword(), authorities);
        }
       throw new UsernameNotFoundException("user not found with email" + username);
    }
}
