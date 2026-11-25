package com.smartcare.consultation;

import com.smartcare.appointment.Appointment;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(name = "room_name", unique = true)
    private String roomName;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "requested_exams", columnDefinition = "TEXT")
    private String requestedExams;

    public Long getId() { return id; }
    public Appointment getAppointment() { return appointment; }
    public String getRoomName() { return roomName; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public String getDiagnosis() { return diagnosis; }
    public String getNotes() { return notes; }
    public String getRequestedExams() { return requestedExams; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
}
