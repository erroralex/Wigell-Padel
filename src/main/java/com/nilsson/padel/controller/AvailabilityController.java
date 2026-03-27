package com.nilsson.padel.controller;

import com.nilsson.padel.dto.AvailableTimeResponse;
import com.nilsson.padel.service.AvailabilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>AvailabilityController</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Exponerar REST-endpoint för att hämta lediga bokningstider för en specifik padelbana och ett valt datum.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Tar emot förfrågningar om tillgängliga tider</li>
 * <li>Vidarebefordrar anrop till {@code AvailabilityService}</li>
 * <li>Returnerar lediga tider som HTTP-svar</li>
 * <li>Skyddar endpointen med rollbaserad åtkomst</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @RestController} som hanterar inkommande API-anrop för tillgänglighet.</p>
 * ──────────────────────────────────────────────
 */
@RestController
@RequestMapping("/api/v1/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<AvailableTimeResponse>> getAvailability(
            @RequestParam LocalDate date,
            @RequestParam Long courtId) {

        List<AvailableTimeResponse> availableTimes = availabilityService.getAvailableTimes(date, courtId);
        return ResponseEntity.ok(availableTimes);
    }

}