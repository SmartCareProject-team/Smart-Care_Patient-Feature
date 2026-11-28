package com.smartcare.patient;

import com.smartcare.config.ConflictException;
import com.smartcare.config.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional(readOnly = true)
    public PatientResponse getByEmail(String email) {
        return PatientResponse.from(find(email));
    }

    @Transactional
    public PatientResponse update(String email, PatientUpdateRequest req) {
        Patient p = find(email);

        if (!p.getCin().equals(req.cin()) && patientRepository.existsByCin(req.cin())) {
            throw new ConflictException("CIN already used");
        }

        p.setFirstName(req.firstName());
        p.setLastName(req.lastName());
        p.setCin(req.cin());
        p.setBirthDate(req.birthDate());
        p.setPhone(req.phone());
        p.setAddress(req.address());
        p.setBloodType(req.bloodType());
        p.setMedicalHistory(req.medicalHistory());
        p.setAllergies(req.allergies());

        return PatientResponse.from(patientRepository.save(p));
    }

    private Patient find(String email) {
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }
}