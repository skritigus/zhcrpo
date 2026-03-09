package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.core.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByName(String name);
    User findById(Long id);
    User findByPhoneNumber(String phoneNumber);

    User createUser(User user);
}
