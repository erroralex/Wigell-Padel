package com.nilsson.padel.service;

import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.AvailableTimeResponse;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CourtRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>AvailabilityService</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Hanterar logik för att hämta lediga bokningsbara tider för en specifik padelbana och ett valt datum.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Verifierar att padelbanan existerar</li>
 * <li>Hämtar redan bokade tider för dagen</li>
 * <li>Returnerar alla lediga heltimmespass inom anläggningens öppettider</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @Service} som använder repositories för att sammanställa tillgängliga tider.</p>
 * ──────────────────────────────────────────────
 */
@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;

    private static final LocalTime OPENING_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

    public AvailabilityService(BookingRepository bookingRepository, CourtRepository courtRepository) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
    }

    public List<AvailableTimeResponse> getAvailableTimes(LocalDate date, Long courtId) {
        if (!courtRepository.existsById(courtId)) {
            throw new ResourceNotFoundException("Padelbana med ID " + courtId + " hittades inte.");
        }

        List<Booking> existingBookings = bookingRepository.findByCourtIdAndBookingDate(courtId, date);

        List<LocalTime> bookedTimes = existingBookings.stream()
                .map(Booking::getStartTime)
                .toList();

        List<AvailableTimeResponse> availableTimes = new ArrayList<>();
        LocalTime currentTime = OPENING_TIME;

        while (currentTime.isBefore(CLOSING_TIME)) {
            if (!bookedTimes.contains(currentTime)) {
                availableTimes.add(new AvailableTimeResponse(courtId, date, currentTime));
            }
            currentTime = currentTime.plusHours(1);
        }

        return availableTimes;
    }

}