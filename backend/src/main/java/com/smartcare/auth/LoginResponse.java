package com.smartcare.auth;

public record LoginResponse(
        String token,
        Role role,
        Long userId,
        Long patientId
) {}