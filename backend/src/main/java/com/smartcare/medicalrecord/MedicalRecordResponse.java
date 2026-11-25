package com.smartcare.medicalrecord;

import com.smartcare.patient.PatientResponse;
import java.util.List;

public record MedicalRecordResponse(PatientResponse patient, List<ConsultationSummary> consultations) {}