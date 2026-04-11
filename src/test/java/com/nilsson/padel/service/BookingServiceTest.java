package com.nilsson.padel.service;

import com.groupc.shared.client.CurrencyClient;
import com.nilsson.padel.dto.BookingRequest;
import com.nilsson.padel.dto.BookingResponse;
import com.nilsson.padel.entity.Court;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CourtRepository;
import com.nilsson.padel.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <h2>BookingServiceTest</h2>
 * <p>Testklass för {@link BookingService}.</p>
 * <p>Denna klass innehåller enhetstester för att verifiera funktionaliteten i {@code BookingService},
 * inklusive skapande av bokningar, valutaomvandling och säkerhetskontroller för åtkomstbehörighet.</p>
 *
 * <h3>Funktioner som testas:</h3>
 * <ul>
 *     <li>Skapa en bokning med korrekt valutaomvandling när användaren är ägare till kundkontot.</li>
 *     <li>Förhindra att en användare skapar en bokning för en annan kund (åtkomstnekad).</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CourtRepository courtRepository;
    @Mock
    private CurrencyClient currencyClient;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createBooking_ShouldCalculateCurrencyAndSave_WhenUserIsOwner() {
        // Arrange
        Long customerId = 1L;
        Long courtId = 1L;
        String keycloakId = "kc-user-id";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(keycloakId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        Customer customer = new Customer("testuser", keycloakId, "Test", "User", null);
        Court court = new Court("Court 1", "Desc", true, new BigDecimal("400.00"));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(courtRepository.findById(courtId)).thenReturn(Optional.of(court));
        when(currencyClient.getExchangeRate("SEK", "EUR")).thenReturn(0.088);

        Booking savedBooking = new Booking(customer, court, LocalDate.now(), LocalTime.NOON, 4, new BigDecimal("400.00"));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        BookingRequest request = new BookingRequest(customerId, courtId, LocalDate.now(), LocalTime.NOON, 4);

        // Act
        BookingResponse response = bookingService.createBooking(request);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("400.00"), response.totalPriceSek());
        verify(currencyClient, times(1)).getExchangeRate("SEK", "EUR");
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowAccessDenied_WhenUserTriesToBookForSomeoneElse() {
        // Arrange
        Long targetCustomerId = 2L;
        String currentKeycloakId = "kc-hacker-id";
        String targetKeycloakId = "kc-victim-id";

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentKeycloakId);
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        Customer targetCustomer = new Customer("victim", targetKeycloakId, "Vic", "Tim", null);
        when(customerRepository.findById(targetCustomerId)).thenReturn(Optional.of(targetCustomer));

        BookingRequest request = new BookingRequest(targetCustomerId, 1L, LocalDate.now(), LocalTime.NOON, 4);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }
}