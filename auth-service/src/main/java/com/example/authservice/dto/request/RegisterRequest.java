package com.example.authservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Username must not be empty")
    private String username;

    private String firstName;

    private String lastName;

    @Email(message = "Malformed email")
    @NotBlank(message = "Email must not be null")
    private String email;

    @NotBlank(message = "Password must not be null")
    private String password;

    @NotBlank(message = "Role can not be null")
    @Pattern(regexp = "ADMIN|MANAGER|USER", message = "The role must be ADMIN, MANAGER or USER")
    private String role;
}
