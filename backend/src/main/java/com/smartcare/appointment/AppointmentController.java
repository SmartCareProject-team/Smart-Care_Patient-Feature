package com.smartcare.appointment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients/me/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<AppointmentResponse> list(Authentication auth) {
        return appointmentService.list(auth.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(Authentication auth, @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.create(auth.getName(), request);
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(Authentication auth, @PathVariable Long id,
                                      @Valid @RequestBody AppointmentRequest request) {
        return appointmentService.update(auth.getName(), id, request);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(Authentication auth, @PathVariable Long id) {
        return appointmentService.cancel(auth.getName(), id);
    }
}