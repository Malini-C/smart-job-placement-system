package com.placementsystem.service.impl;

import com.placementsystem.dto.request.LoginRequest;
import com.placementsystem.dto.request.RegisterRequest;
import com.placementsystem.dto.response.AuthResponse;
import com.placementsystem.entity.User;
import com.placementsystem.exception.BadRequestException;
import com.placementsystem.repository.UserRepository;
import com.placementsystem.security.JwtTokenProvider;
import com.placementsystem.service.AuthService;
import com.placementsystem.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set role
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.STUDENT);
        }

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                null,
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole().name(),
                "Registration successful"
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                "Login successful"
        );
    }
}