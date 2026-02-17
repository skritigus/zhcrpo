package com.bootgussy.dancecenterservice.core.service;

import com.bootgussy.dancecenterservice.api.dto.create.LoginRequest;

public interface AuthService {
    void register(LoginRequest request, String role);
}
