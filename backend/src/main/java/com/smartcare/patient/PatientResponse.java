package com.smartcare.patient;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String cin,
        LocalDate birthDate,
        String phone,
        String address,
        String bloodType,
        String medicalHistory,
        String allergies
) {
    public static PatientResponse from(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getUser().getEmail(),
                p.getFirstName(),
                p.getLastName(),
                p.getCin(),
                p.getBirthDate(),
                p.getPhone(),
                p.getAddress(),
                p.getBloodType(),
                p.getMedicalHistory(),
                p.getAllergies());
    }
}