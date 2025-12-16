package com.nexuscart.auth.service;

import com.nexuscart.auth.entity.RefreshToken;
import com.nexuscart.auth.entity.User;
import com.nexuscart.auth.repository.RefreshTokenRepository;
import com.nexuscart.auth.repository.UserRepository;
import com.nexuscart.dto.auth.AuthResponse;
import com.nexuscart.dto.auth.LoginRequest;
import com.nexuscart.dto.auth.RegisterRequest;
import com.nexuscart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Authentication Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(User.Role.ROLE_CUSTOMER))
                .enabled(true)
                .accountNonLocked(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered: {}", user.getEmail());

        return generateAuthResponse(user);
    }

    /**
     * Authenticate user and generate tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        if (!user.isAccountNonLocked()) {
            throw new RuntimeException("Account is locked");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Revoke existing refresh tokens
        refreshTokenRepository.revokeAllUserTokens(user);

        log.info("User logged in: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional
    public AuthResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        User user = refreshToken.getUser();

        // Revoke the used refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Token refreshed for user: {}", user.getEmail());
        return generateAuthResponse(user);
    }

    /**
     * Logout user by revoking all tokens
     */
    @Transactional
    public void logout(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("User logged out: {}", user.getEmail());
    }

    private AuthResponse generateAuthResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        String accessToken = jwtUtil.generateAccessToken(
                user.getId().toString(),
                user.getEmail(),
                roles);

        String refreshTokenString = jwtUtil.generateRefreshToken(user.getId().toString());

        // Save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenString)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpiration())
                .userId(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .build();
    }
}
