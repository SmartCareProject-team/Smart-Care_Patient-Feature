package com.smartcare.doctor;

public record DoctorResponse(Long id, String firstName, String lastName, Long specialtyId, String specialtyName) {
    public static DoctorResponse from(Doctor d) {
        return new DoctorResponse(d.getId(), d.getFirstName(), d.getLastName(),
                d.getSpecialty().getId(), d.getSpecialty().getName());
    }
}