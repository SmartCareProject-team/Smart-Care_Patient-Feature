package com.smartcare.patient;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PatientUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String cin,
        LocalDate birthDate,
        String phone,
        String address,
        String bloodType,
        String medicalHistory,
        String allergies
) {}