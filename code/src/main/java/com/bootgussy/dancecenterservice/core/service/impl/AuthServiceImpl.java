package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.api.dto.create.LoginRequest;
import com.bootgussy.dancecenterservice.core.exception.IncorrectDataException;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.Role;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.RoleRepository;
import com.bootgussy.dancecenterservice.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

public class AuthServiceImpl {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public void register(LoginRequest request, String roleName) {
        User user = new User();
        user.setName(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IncorrectDataException("Role" + roleName + "doesn't exist"));

        user.setRoles(Collections.singletonList(userRole));
        userRepository.save(user);
    }
}
