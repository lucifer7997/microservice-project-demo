package com.example.authservice.service;

import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.response.ApiResponse;
import com.example.authservice.entity.Role;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest registerRequest) {
        try {
            User user = User.builder()
                    .username(registerRequest.getUsername())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .firstName(registerRequest.getFirstName())
                    .lastName(registerRequest.getLastName())
                    .role(Role.toEnum(registerRequest.getRole()))
                    .build();

            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ApiResponse login(RegisterRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
        if (user.isPresent()) {
            User foundUser = user.get();
            String accessToken = jwtService.generateToken(foundUser);
            String refreshToken = jwtService.generateRefreshToken(foundUser);

            foundUser.setAccessToken(accessToken);
            foundUser.setRefreshToken(refreshToken);
            userRepository.save(foundUser);

            return new ApiResponse(HttpStatus.OK, "Login successful", Map.of("accessToken", accessToken, "refreshToken", refreshToken));
        } else {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "User not found", null);
        }
    }

    public ApiResponse refreshToken(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "Invalid token", null);
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        if (!StringUtils.hasText(username)) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "Invalid token", null);
        }
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "User not found", null);
        }

        User foundUser = user.get();
        if (!StringUtils.hasText(foundUser.getAccessToken()) || !StringUtils.hasText(foundUser.getRefreshToken())) {
            return new ApiResponse(HttpStatus.UNAUTHORIZED, "Token Removed", null);
        }

        String accessToken = jwtService.generateToken(foundUser);
        String refreshToken = jwtService.generateRefreshToken(foundUser);

        foundUser.setAccessToken(accessToken);
        foundUser.setRefreshToken(refreshToken);
        userRepository.save(foundUser);

        return new ApiResponse(HttpStatus.OK, "Token refreshed", Map.of("accessToken", accessToken, "refreshToken", refreshToken));
    }
}
