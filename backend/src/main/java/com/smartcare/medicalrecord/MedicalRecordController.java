package com.smartcare.medicalrecord;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/me/medical-record")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public MedicalRecordResponse get(Authentication auth) {
        return medicalRecordService.get(auth.getName());
    }
}