package com.testproject.taskmanager.auth.service;

import com.testproject.taskmanager.auth.dto.AuthResponse;
import com.testproject.taskmanager.auth.dto.LoginRequest;
import com.testproject.taskmanager.auth.dto.RefreshTokenRequest;
import com.testproject.taskmanager.auth.dto.SignupRequest;
import com.testproject.taskmanager.auth.exception.InvalidCredentialsException;
import com.testproject.taskmanager.auth.exception.UserAlreadyExistsException;
import com.testproject.taskmanager.auth.model.User;
import com.testproject.taskmanager.auth.repository.UserRepository;
import com.testproject.taskmanager.common.security.JwtUtil;
import com.testproject.taskmanager.common.security.TokenBlacklistService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            TokenBlacklistService tokenBlacklistService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistsException(signupRequest.getUsername());
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        User saved = userRepository.save(user);
        return new AuthResponse(saved.getId(), saved.getUsername(),null, null);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId());
        return new AuthResponse(user.getId(), user.getUsername(), accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        if (jwtUtil.isRefreshTokenInValid(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        if (tokenBlacklistService.inBlacklist(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        Long userId = jwtUtil.extractUserIdFromRefreshToken(refreshToken);
        String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken);

        String newAccessToken = jwtUtil.generateAccessToken(username, userId);
        return new AuthResponse(userId, username, newAccessToken, refreshToken);
    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (jwtUtil.isRefreshTokenInValid(refreshToken)) {
            throw new InvalidCredentialsException();
        }

        long remainingMillis = jwtUtil.getRefreshTokenRemainingMillis(refreshToken);
        tokenBlacklistService.blacklist(refreshToken, remainingMillis);
    }
}
