package com.testproject.taskmanager.auth.service;

import com.testproject.taskmanager.auth.dto.AuthResponse;
import com.testproject.taskmanager.auth.dto.LoginRequest;
import com.testproject.taskmanager.auth.dto.SignupRequest;
import com.testproject.taskmanager.auth.exception.InvalidCredentialsException;
import com.testproject.taskmanager.auth.exception.UserAlreadyExistsException;
import com.testproject.taskmanager.auth.model.User;
import com.testproject.taskmanager.auth.repository.UserRepository;
import com.testproject.taskmanager.common.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistsException(signupRequest.getUsername());
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        User saved = userRepository.save(user);
        return new AuthResponse(saved.getId(), saved.getUsername(),null);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        return new AuthResponse(user.getId(), user.getUsername(), token);
    }
}
