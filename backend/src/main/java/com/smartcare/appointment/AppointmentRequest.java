package com.smartcare.appointment;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotNull Long doctorId,
        @NotNull LocalDateTime dateTime,
        @NotNull AppointmentType type,
        String reason
) {}