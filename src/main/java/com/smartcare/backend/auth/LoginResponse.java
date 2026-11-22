package com.smartcare.backend.auth;

public record LoginResponse(
        String token,
        Role role,
        Long userId,
        Long patientId
) {}