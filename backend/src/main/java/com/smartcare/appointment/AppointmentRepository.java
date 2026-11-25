package com.smartcare.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByDateTimeAsc(Long patientId);

    Optional<Appointment> findByIdAndPatientId(Long id, Long patientId);

    boolean existsByDoctorIdAndDateTimeAndStatusNot(Long doctorId, LocalDateTime dateTime, AppointmentStatus status);

    boolean existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(Long doctorId, LocalDateTime dateTime, AppointmentStatus status, Long id);
}