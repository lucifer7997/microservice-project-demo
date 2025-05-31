package com.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.RegisterRequest;
import com.example.dto.response.AuthenticationResponse;
import com.example.dto.response.RegisterResponse;
import com.example.services.AuthenticationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api/v1/auth")
public class AuthenticationController {
    public final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        RegisterResponse registerResponse = authenticationService.register(registerRequest);
        return ResponseEntity
                .status(registerResponse.getStatus())
                .body(registerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        AuthenticationResponse authenticationResponse = authenticationService.login(loginRequest);
        return ResponseEntity
                .status(authenticationResponse.getStatus())
                .body(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public AuthenticationResponse refreshToken(@RequestHeader("Authorization") String authHeader) {
        return authenticationService.refreshToken(authHeader);
    }

}
