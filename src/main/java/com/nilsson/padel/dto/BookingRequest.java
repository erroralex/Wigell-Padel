package com.nilsson.padel.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(
        Long customerId,
        Long courtId,
        LocalDate bookingDate,
        LocalTime startTime,
        Integer numberOfPlayers
) {
}
