package com.nilsson.padel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record BookingResponse(
        Long id,
        Long customerId,
        Long courtId,
        String courtName,
        LocalDate bookingDate,
        LocalTime startTime,
        Integer numberOfPlayers,
        BigDecimal totalPriceSek,
        BigDecimal totalPriceEur
) {
}
