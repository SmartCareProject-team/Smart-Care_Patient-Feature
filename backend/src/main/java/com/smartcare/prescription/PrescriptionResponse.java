package com.smartcare.prescription;

import java.time.LocalDateTime;
import java.util.List;

public record PrescriptionResponse(
        Long id,
        LocalDateTime issuedAt,
        String doctorName,
        List<PrescriptionItemResponse> items
) {
    public static PrescriptionResponse from(Prescription p) {
        var doctor = p.getConsultation().getAppointment().getDoctor();
        return new PrescriptionResponse(
                p.getId(),
                p.getIssuedAt(),
                doctor.getFirstName() + " " + doctor.getLastName(),
                p.getItems().stream().map(PrescriptionItemResponse::from).toList());
    }
}