package com.smartcare.prescription;

public record PrescriptionItemResponse(String medication, String dosage, String frequency, String duration) {
    public static PrescriptionItemResponse from(PrescriptionItem i) {
        return new PrescriptionItemResponse(i.getMedication(), i.getDosage(), i.getFrequency(), i.getDuration());
    }
}