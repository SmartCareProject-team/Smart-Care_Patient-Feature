package com.smartcare.consultation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByAppointmentPatientIdOrderByAppointmentDateTimeDesc(Long patientId);

    Optional<Consultation> findByAppointmentId(Long appointmentId);
}