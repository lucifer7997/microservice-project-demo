package com.example.authservice.entity;

public enum Role {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_MODERATOR;

    public static Role toEnum(String role) {
        for (Role r : Role.values()) {
            if (r.name().equals(role)) {
                return r;
            }
        }
        return null;
    }
}
