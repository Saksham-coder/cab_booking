package com.saksham.service;

import com.saksham.exception.UserException;
import com.saksham.model.Ride;
import com.saksham.model.User;

import java.util.List;

public interface UserService {

    public User createUser(User user) throws UserException;

    public User getReqUserProfile(String token) throws UserException;

    public User findUserById(Integer Id) throws UserException;

    public User findUserByEmail(String email) throws UserException;

    public User findUserByToken(String token) throws UserException;

    public List<Ride> completedRides(Integer userId) throws UserException;


}
