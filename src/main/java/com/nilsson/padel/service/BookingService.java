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
import org.springframework.stereotype.Service;

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
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + request.customerId() + " hittades inte."));

        Court court = courtRepository.findById(request.courtId())
                .orElseThrow(() -> new ResourceNotFoundException("Padelbana med ID " + request.courtId() + " hittades inte."));

        BigDecimal priceSek = court.getPrice();
        double exchangeRate = currencyClient.getExchangeRate("SEK", "EUR");
        BigDecimal priceEur = priceSek.multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, RoundingMode.HALF_UP);

        Booking newBooking = new Booking(
                customer,
                court,
                request.bookingDate(),
                request.startTime(),
                request.numberOfPlayers(),
                priceSek
        );
        newBooking.setTotalPriceEuro(priceEur);

        Booking savedBooking = bookingRepository.save(newBooking);
        return mapToResponse(savedBooking);
    }

    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med ID " + id + " hittades inte."));

        existingBooking.setBookingDate(request.bookingDate());
        existingBooking.setStartTime(request.startTime());
        existingBooking.setNumberOfPlayers(request.numberOfPlayers());

        if (!existingBooking.getCourt().getId().equals(request.courtId())) {
            Court newCourt = courtRepository.findById(request.courtId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ny padelbana med ID " + request.courtId() + " hittades inte."));

            existingBooking.setCourt(newCourt);

            BigDecimal newPriceSek = newCourt.getPrice();
            double exchangeRate = currencyClient.getExchangeRate("SEK", "EUR");
            BigDecimal newPriceEur = newPriceSek.multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, RoundingMode.HALF_UP);

            existingBooking.setTotalPriceSek(newPriceSek);
            existingBooking.setTotalPriceEuro(newPriceEur);
        }

        Booking updatedBooking = bookingRepository.save(existingBooking);
        return mapToResponse(updatedBooking);
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med ID " + id + " hittades inte."));
        return mapToResponse(booking);
    }

    public List<BookingResponse> getBookingsByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte.");
        }
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kunde inte radera: Bokning med ID " + id + " hittades inte.");
        }
        bookingRepository.deleteById(id);
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
                booking.getTotalPriceEuro()
        );
    }
}
