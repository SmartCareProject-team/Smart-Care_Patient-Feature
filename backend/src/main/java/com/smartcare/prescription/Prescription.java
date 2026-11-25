package com.smartcare.prescription;

import com.smartcare.consultation.Consultation;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consultation_id", nullable = false, unique = true)
    private Consultation consultation;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @OneToMany(mappedBy = "prescription", fetch = FetchType.LAZY)
    private List<PrescriptionItem> items = new ArrayList<>();

    public Long getId() { return id; }
    public Consultation getConsultation() { return consultation; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public String getInstructions() { return instructions; }
    public List<PrescriptionItem> getItems() { return items; }
}