package com.nilsson.padel.service;

import com.groupc.shared.client.CurrencyClient;
import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.BookingRequest;
import com.nilsson.padel.dto.BookingResponse;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.entity.Court;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CourtRepository;
import com.nilsson.padel.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>BookingService</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Hanterar affärslogik för bokningar av padelbanor, inklusive skapande, uppdatering, hämtning och radering.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Skapar och uppdaterar bokningar för kund och bana</li>
 * <li>Beräknar totalpris i SEK och konverterar till EUR</li>
 * <li>Hämtar enskilda bokningar, kunders bokningar eller alla bokningar</li>
 * <li>Validerar att kund, bana och bokning existerar</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @Service} som samordnar repositories, DTO-mappning och valutakonvertering via extern klient.</p>
 * ──────────────────────────────────────────────
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final CourtRepository courtRepository;
    private final CurrencyClient currencyClient;

    private final static Logger logger = LoggerFactory.getLogger(BookingService.class);

    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          CourtRepository courtRepository,
                          CurrencyClient currencyClient) {

        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.courtRepository = courtRepository;
        this.currencyClient = currencyClient;
    }

    public BookingResponse createBooking(BookingRequest request) {
        validateUserOwnership(request.customerId());

        logger.info("Skapar ny bokning: Kund={}, Bana={}, Datum={}",
                request.customerId(), request.courtId(), request.bookingDate());

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + request.customerId() + " hittades inte."));

        Court court = courtRepository.findById(request.courtId())
                .orElseThrow(() -> new ResourceNotFoundException("Padelbana med ID " + request.courtId() + " hittades inte."));

        BigDecimal priceSek = court.getPrice();

        logger.debug("Anropar valutatjänst för konvertering SEK -> EUR");
        double exchangeRate = currencyClient.getExchangeRate("SEK", "EUR");

        BigDecimal priceEur = priceSek.multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, RoundingMode.HALF_UP);
        logger.info("Bokar bana {} för kund {}. Pris: {} SEK. Växelkurs: {}",
                request.courtId(), request.customerId(), priceSek, exchangeRate);


        Booking newBooking = new Booking(
                customer,
                court,
                request.bookingDate(),
                request.startTime(),
                request.numberOfPlayers(),
                priceSek
        );

        newBooking.setTotalPriceEur(priceEur);

        Booking savedBooking = bookingRepository.save(newBooking);

        logger.info("Bokning sparad med ID: {}", savedBooking.getId());

        return mapToResponse(savedBooking);
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        logger.info("Uppdaterar bokning ID: {}", id);

        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med ID " + id + " hittades inte."));

        validateUserOwnership(existingBooking.getCustomer().getId());

        existingBooking.setBookingDate(request.bookingDate());
        existingBooking.setStartTime(request.startTime());
        existingBooking.setNumberOfPlayers(request.numberOfPlayers());

        if (!existingBooking.getCourt().getId().equals(request.courtId())) {
            logger.info("Byte av bana upptäckt i bokning {}. Beräknar om pris.", id);

            Court newCourt = courtRepository.findById(request.courtId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ny padelbana med ID " + request.courtId() + " hittades inte."));

            existingBooking.setCourt(newCourt);

            BigDecimal newPriceSek = newCourt.getPrice();
            double exchangeRate = currencyClient.getExchangeRate("SEK", "EUR");
            BigDecimal newPriceEur = newPriceSek.multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, RoundingMode.HALF_UP);

            existingBooking.setTotalPriceSek(newPriceSek);
            existingBooking.setTotalPriceEur(newPriceEur);

            logger.debug("Nytt pris för bokning {}: {} SEK", id, newPriceSek);
        }

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return mapToResponse(updatedBooking);
    }

    public BookingResponse getBookingById(Long id) {
        logger.info("Hämtar bokning med ID: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med ID " + id + " hittades inte."));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        logger.info("Hämtar alla bokningar för kund: {}", customerId);

        validateUserOwnership(customerId);

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte.");
        }
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BookingResponse> getAllBookings() {
        logger.info("Hämtar alla bokningar: ");
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteBooking(Long id) {
        logger.warn("Raderar bokning med ID: {}", id);

        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kunde inte radera: Bokning med ID " + id + " hittades inte.");
        }
        bookingRepository.deleteById(id);
        logger.info("Bokning {} raderad framgångsrikt.", id);
    }

    private void validateUserOwnership(Long targetCustomerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return;
        }

        String currentKeycloakId = auth.getName();

        Customer targetCustomer = customerRepository.findById(targetCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kunden hittades inte."));

        if (!targetCustomer.getKeycloakId().equals(currentKeycloakId)) {
            throw new AccessDeniedException("Åtkomst nekad: Du har inte behörighet att hantera denna resurs.");
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCourt().getId(),
                booking.getCourt().getName(),
                booking.getBookingDate(),
                booking.getStartTime(),
                booking.getNumberOfPlayers(),
                booking.getTotalPriceSek(),
                booking.getTotalPriceEur()
        );
    }
}
