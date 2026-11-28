package com.smartcare.medicalrecord;

import com.smartcare.config.NotFoundException;
import com.smartcare.consultation.ConsultationRepository;
import com.smartcare.patient.Patient;
import com.smartcare.patient.PatientRepository;
import com.smartcare.patient.PatientResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicalRecordService {

    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;

    public MedicalRecordService(PatientRepository patientRepository,
                                ConsultationRepository consultationRepository) {
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse get(String email) {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        var consultations = consultationRepository
                .findByAppointmentPatientIdOrderByAppointmentDateTimeDesc(patient.getId())
                .stream().map(ConsultationSummary::from).toList();
        return new MedicalRecordResponse(PatientResponse.from(patient), consultations);
    }
}