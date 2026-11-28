package com.smartcare.appointment;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long doctorId,
        String doctorName,
        String specialtyName,
        LocalDateTime dateTime,
        AppointmentType type,
        AppointmentStatus status,
        String reason
) {
    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getDoctor().getId(),
                a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName(),
                a.getDoctor().getSpecialty().getName(),
                a.getDateTime(),
                a.getType(),
                a.getStatus(),
                a.getReason());
    }
}