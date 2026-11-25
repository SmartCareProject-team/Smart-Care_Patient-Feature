package com.smartcare.prescription;

import com.smartcare.config.NotFoundException;
import com.smartcare.patient.Patient;
import com.smartcare.patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientRepository patientRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> list(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        return prescriptionRepository
                .findByConsultationAppointmentPatientIdOrderByIssuedAtDesc(patient.getId())
                .stream().map(PrescriptionResponse::from).toList();
    }
}