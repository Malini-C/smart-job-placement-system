package com.placementsystem.service;

import com.placementsystem.dto.request.LoginRequest;
import com.placementsystem.dto.request.RegisterRequest;
import com.placementsystem.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}