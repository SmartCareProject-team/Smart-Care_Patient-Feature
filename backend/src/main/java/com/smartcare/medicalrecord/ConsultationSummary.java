package com.smartcare.medicalrecord;

import com.smartcare.consultation.Consultation;
import java.time.LocalDateTime;

public record ConsultationSummary(
        LocalDateTime date,
        String doctorName,
        String diagnosis,
        String notes,
        String requestedExams
) {
    public static ConsultationSummary from(Consultation c) {
        var a = c.getAppointment();
        return new ConsultationSummary(
                a.getDateTime(),
                a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName(),
                c.getDiagnosis(),
                c.getNotes(),
                c.getRequestedExams());
    }
}