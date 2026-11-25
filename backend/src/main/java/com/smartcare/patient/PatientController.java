package com.smartcare.patient;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/me")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public PatientResponse getMe(Authentication auth) {
        return patientService.getByEmail(auth.getName());
    }

    @PutMapping
    public PatientResponse updateMe(Authentication auth,
                                    @Valid @RequestBody PatientUpdateRequest request) {
        return patientService.update(auth.getName(), request);
    }
}