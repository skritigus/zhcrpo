package com.bootgussy.dancecenterservice.core.service.impl;

import com.bootgussy.dancecenterservice.api.dto.create.LoginRequest;
import com.bootgussy.dancecenterservice.api.dto.create.RegisterRequest;
import com.bootgussy.dancecenterservice.api.dto.response.JwtResponse;
import com.bootgussy.dancecenterservice.core.config.JwtUtils;
import com.bootgussy.dancecenterservice.core.exception.ResourceNotFoundException;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.RefreshTokenRepository;
import com.bootgussy.dancecenterservice.core.repository.RoleRepository;
import com.bootgussy.dancecenterservice.core.service.AuthService;
import com.bootgussy.dancecenterservice.core.service.TokenService;
import com.bootgussy.dancecenterservice.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           TokenService tokenService,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           UserService userService,
                           JwtUtils jwtUtils,
                           RefreshTokenRepository refreshTokenRepository){
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String registerUser(RegisterRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getUsername());
        //user.setEmail(signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        /*Set<String> strRoles = signUpRequest.roles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                Role foundRole = roleRepository.findByName("ROLE_" + role.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found."));
                roles.add(foundRole);
            });
        }*/

        user.setRoles(List.of(roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Error"))
        ));

        userService.createUser(user);

        return "User registered successfully!";
    }


    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getPhoneNumber(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        return tokenService.generateAuthResponse(user);
    }

    public ResponseCookie generateRefreshCookie(Long id) {
        String refreshToken = jwtUtils.generateRefreshToken(userService.findById(id));
        refreshTokenRepository.save(id, refreshToken, 7);

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }
}
