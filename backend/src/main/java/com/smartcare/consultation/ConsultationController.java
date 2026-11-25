package com.smartcare.consultation;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping("/{appointmentId}/room")
    public RoomResponse room(Authentication auth, @PathVariable Long appointmentId) {
        return consultationService.getRoom(auth.getName(), appointmentId);
    }
}