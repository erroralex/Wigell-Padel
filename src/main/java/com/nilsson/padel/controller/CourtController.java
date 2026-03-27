package com.nilsson.padel.controller;

import com.nilsson.padel.dto.CourtRecord;
import com.nilsson.padel.service.CourtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/courts")
public class CourtController {

    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CourtRecord>> getAllCourts() {
        return ResponseEntity.ok(courtService.getAllCourts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtRecord> getCourtById(@PathVariable Long id) {
        return ResponseEntity.ok(courtService.getCourtById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtRecord> createCourt(@RequestBody CourtRecord request) {
        CourtRecord createdCourt = courtService.createCourt(request);

        URI location = URI.create("/api/v1/courts/" + createdCourt.id());
        return ResponseEntity.created(location).body(createdCourt);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtRecord> updateCourt(@PathVariable Long id, @RequestBody CourtRecord request) {
        CourtRecord updatedCourt = courtService.updateCourt(id, request);
        return ResponseEntity.ok(updatedCourt);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.noContent().build();
    }

}