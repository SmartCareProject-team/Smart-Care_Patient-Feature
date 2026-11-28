package com.smartcare.appointment;

import com.smartcare.config.ConflictException;
import com.smartcare.config.NotFoundException;
import com.smartcare.doctor.Doctor;
import com.smartcare.doctor.DoctorRepository;
import com.smartcare.patient.Patient;
import com.smartcare.patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> list(String email) {
        Patient patient = findPatient(email);
        return appointmentRepository.findByPatientIdOrderByDateTimeAsc(patient.getId())
                .stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse create(String email, AppointmentRequest req) {
        Patient patient = findPatient(email);
        Doctor doctor = findDoctor(req.doctorId());

        if (appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNot(
                doctor.getId(), req.dateTime(), AppointmentStatus.CANCELLED)) {
            throw new ConflictException("Doctor already booked at this date and time");
        }

        Appointment a = new Appointment();
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setDateTime(req.dateTime());
        a.setType(req.type());
        a.setReason(req.reason());
        a.setStatus(AppointmentStatus.PENDING);

        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse update(String email, Long id, AppointmentRequest req) {
        Patient patient = findPatient(email);
        Appointment a = findOwned(id, patient);
        requirePending(a);

        Doctor doctor = findDoctor(req.doctorId());

        if (appointmentRepository.existsByDoctorIdAndDateTimeAndStatusNotAndIdNot(
                doctor.getId(), req.dateTime(), AppointmentStatus.CANCELLED, a.getId())) {
            throw new ConflictException("Doctor already booked at this date and time");
        }

        a.setDoctor(doctor);
        a.setDateTime(req.dateTime());
        a.setType(req.type());
        a.setReason(req.reason());

        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse cancel(String email, Long id) {
        Patient patient = findPatient(email);
        Appointment a = findOwned(id, patient);
        requirePending(a);

        a.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(appointmentRepository.save(a));
    }

    private Patient findPatient(String email) {
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    private Appointment findOwned(Long id, Patient patient) {
        return appointmentRepository.findByIdAndPatientId(id, patient.getId())
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
    }

    private void requirePending(Appointment a) {
        if (a.getStatus() != AppointmentStatus.PENDING) {
            throw new ConflictException("Appointment is not PENDING");
        }
    }
}