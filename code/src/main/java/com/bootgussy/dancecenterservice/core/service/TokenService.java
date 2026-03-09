package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.api.dto.response.JwtResponse;
import com.bootgussy.dancecenterservice.core.model.User;

public interface TokenService {
    void deleteToken(String refreshToken);
    JwtResponse generateAuthResponse(User user);
    JwtResponse refreshAccessToken(String refreshToken);
}
