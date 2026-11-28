package com.smartcare.consultation;

import com.smartcare.appointment.Appointment;
import com.smartcare.appointment.AppointmentRepository;
import com.smartcare.appointment.AppointmentStatus;
import com.smartcare.appointment.AppointmentType;
import com.smartcare.config.ConflictException;
import com.smartcare.config.NotFoundException;
import com.smartcare.patient.Patient;
import com.smartcare.patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConsultationService {

    private static final String JITSI_BASE = "https://meet.jit.si/";

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public ConsultationService(ConsultationRepository consultationRepository,
                               AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository) {
        this.consultationRepository = consultationRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional
    public RoomResponse getRoom(String email, Long appointmentId) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Patient not found"));

        // ownership: un rendez-vous d'un autre patient = 404
        Appointment a = appointmentRepository.findByIdAndPatientId(appointmentId, patient.getId())
                .orElseThrow(() -> new NotFoundException("Appointment not found"));

        if (a.getType() != AppointmentType.ONLINE) {
            throw new ConflictException("Appointment is not ONLINE");
        }
        if (a.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new ConflictException("Appointment is not CONFIRMED");
        }

        Consultation c = consultationRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    Consultation n = new Consultation();
                    n.setAppointment(a);
                    n.setRoomName("smartcare-" + UUID.randomUUID());
                    return consultationRepository.save(n);
                });

        if (c.getRoomName() == null) {
            c.setRoomName("smartcare-" + UUID.randomUUID());
            consultationRepository.save(c);
        }

        return new RoomResponse(JITSI_BASE + c.getRoomName());
    }
}