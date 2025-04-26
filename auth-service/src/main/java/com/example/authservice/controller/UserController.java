package com.example.authservice.controller;

import com.example.authservice.dto.request.RegisterRequest;
import com.example.authservice.dto.response.ApiResponse;
import com.example.authservice.entity.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String username = registerRequest.getUsername();

        Optional<User> userFoundByEmail = userRepository.findByEmail(email);
        Optional<User> userFoundByUsername = userRepository.findByUsername(username);

        if (userFoundByEmail.isPresent() || userFoundByUsername.isPresent()) {
            return ResponseEntity.ok().body(new ApiResponse(HttpStatus.BAD_REQUEST, "User with this email or username already exists", null));
        }

        authenticationService.register(registerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(HttpStatus.OK, "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest loginRequest) {
        ApiResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        ApiResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok().body(response);
    }
}
