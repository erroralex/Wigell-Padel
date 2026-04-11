package com.nilsson.padel.service;

import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.AvailableTimeResponse;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.entity.Court;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CourtRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * <h2>AvailabilityServiceTest</h2>
 * <p>Testklass för {@link AvailabilityService}.</p>
 * <p>Denna klass innehåller enhetstester för att verifiera funktionaliteten i {@code AvailabilityService},
 * som hanterar tillgänglighet för padelbanor.</p>
 *
 * <h3>Funktioner som testas:</h3>
 * <ul>
 *     <li>Hämta alla obokade tider för en specifik bana och datum.</li>
 *     <li>Korrekt hantering när en bana inte hittas (kastar {@link com.groupc.shared.exception.ResourceNotFoundException}).</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void getAvailableTimes_ShouldReturnAllUnbookedHours() {
        Long courtId = 1L;
        LocalDate date = LocalDate.of(2026, 4, 14);

        when(courtRepository.existsById(courtId)).thenReturn(true);

        Booking existingBooking = new Booking(
                new Customer("user", "key-123", "Test", "Testsson", null),
                new Court("Court 1", "Desc", true, new BigDecimal("400")),
                date,
                LocalTime.of(10, 0),
                4,
                new BigDecimal("400")
        );
        when(bookingRepository.findByCourtIdAndBookingDate(courtId, date))
                .thenReturn(List.of(existingBooking));

        List<AvailableTimeResponse> times = availabilityService.getAvailableTimes(date, courtId);

        assertEquals(13, times.size());

        boolean is10Booked = times.stream().anyMatch(t -> t.availableTime().equals(LocalTime.of(10, 0)));
        assertFalse(is10Booked, "Kl 10:00 ska inte vara tillgänglig.");

        boolean is11Available = times.stream().anyMatch(t -> t.availableTime().equals(LocalTime.of(11, 0)));
        assertTrue(is11Available, "Kl 11:00 ska vara tillgänglig.");
    }

    @Test
    void getAvailableTimes_CourtNotFound_ShouldThrowException() {
        Long courtId = 99L;
        LocalDate date = LocalDate.now();
        when(courtRepository.existsById(courtId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            availabilityService.getAvailableTimes(date, courtId);
        });
    }
}