package com.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.AuthenticationResponse;
import com.example.dto.response.RegisterResponse;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int TOKEN_INDEX = 7;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String userName = registerRequest.getUsername();

        Optional<User> userFoundByEmail = userRepository.findByEmail(email);
        Optional<User> userFoundByUsername = userRepository.findByUsername(userName);

        if (userFoundByEmail.isPresent() || userFoundByUsername.isPresent()) {
            return RegisterResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("User already exists!")
                    .build();
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Password should be encoded
                .role(Role.toEnum(registerRequest.getRole())) // Assuming Role is an enum
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .status(HttpStatus.OK.value())
                .message("User created successfully")
                .build();
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        Optional<User> userFoundByUsername = userRepository.findByUsername(username);
        if (userFoundByUsername.isPresent()) {
            User user = userFoundByUsername.get();
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return AuthenticationResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Login successful")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .build();

        } else {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid credentials")
                    .build();
        }
    }

    public AuthenticationResponse refreshToken(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }

        String refreshToken = authHeader.substring(TOKEN_INDEX);
        String username = jwtService.extractUsername(refreshToken);

        if (!StringUtils.hasText(username)) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();

        }

        Optional<User> userFoundByUsername = userRepository.findByUsername(username);
        if (userFoundByUsername.isEmpty()) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid token")
                    .build();
        }

        User user = userFoundByUsername.get();
        if (!StringUtils.hasText(user.getAccessToken()) || !StringUtils.hasText(user.getRefreshToken())) {
            return AuthenticationResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Token revoked")
                    .build();
        }

        //Generate access token and refresh token
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        user.setAccessToken(accessToken);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        //Response access token and refresh token to client
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .message("Refresh token successfully")
                .status(HttpStatus.OK.value())
                .build();
    }
}
