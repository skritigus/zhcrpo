package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.UserRepository;
import com.bootgussy.dancecenterservice.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByName(String name) {

        return userRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + name));
    }
}
