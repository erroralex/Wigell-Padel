package com.nilsson.padel.dto;

import java.math.BigDecimal;

public record CourtRecord(
        Long id,
        String name,
        String description,
        boolean isIndoor,
        BigDecimal pricePerHourSek
) {
}
