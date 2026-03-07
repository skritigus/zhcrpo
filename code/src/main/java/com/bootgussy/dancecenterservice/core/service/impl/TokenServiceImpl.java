package com.bootgussy.dancecenterservice.core.service.impl;


import com.bootgussy.dancecenterservice.api.dto.response.JwtResponse;
import com.bootgussy.dancecenterservice.core.config.JwtUtils;
import com.bootgussy.dancecenterservice.core.model.Role;
import com.bootgussy.dancecenterservice.core.model.User;
import com.bootgussy.dancecenterservice.core.repository.RefreshTokenRepository;
import com.bootgussy.dancecenterservice.core.service.TokenService;
import com.bootgussy.dancecenterservice.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class TokenServiceImpl implements TokenService {

    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public TokenServiceImpl (JwtUtils jwtUtils,
                             UserService userService,
                             RefreshTokenRepository refreshTokenRepository)
    {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public JwtResponse generateAuthResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        return mapToJwtResponse(user, accessToken, refreshToken);
    }

    @Override
    public JwtResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || !jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token is invalid");
        }

        String userIdStr = refreshTokenRepository.findUserIdByToken(refreshToken);
        if (userIdStr == null) {
            throw new RuntimeException("Refresh token is revoked or already used");
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userService.findById(userId);

        refreshTokenRepository.deleteToken(refreshToken);

        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);

        refreshTokenRepository.save(user.getId(), newRefreshToken,  7);

        return new JwtResponse(newAccessToken, newRefreshToken, user.getName(),
                user.getRoles().stream().map(Role::getName).toList());
    }

    @Override
    public void deleteToken(String refreshToken)
    {
        if (refreshToken != null && jwtUtils.validateToken(refreshToken)) {
            refreshTokenRepository.deleteToken(refreshToken);
        }
    }

    private JwtResponse mapToJwtResponse(User user, String accessToken, String refreshToken) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        return new JwtResponse(accessToken, refreshToken, user.getName(), roles);
    }
}
