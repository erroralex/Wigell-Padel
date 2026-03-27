package com.nilsson.padel.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableTimeResponse(
        Long courtId,
        LocalDate date,
        LocalTime availableTime
) {}