package com.nilsson.padel.service;

import com.groupc.shared.exception.ResourceNotFoundException;
import com.nilsson.padel.dto.AddressRecord;
import com.nilsson.padel.dto.CustomerRequest;
import com.nilsson.padel.dto.CustomerResponse;
import com.nilsson.padel.entity.Address;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.repository.AddressRepository;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CustomerRepository;
import com.nilsson.padel.security.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ──────────────────────────────────────────────
 * <h2>CustomerService</h2>
 * ──────────────────────────────────────────────
 * <p><strong>Ansvar:</strong> Hanterar affärslogik för kunder och deras adresser, inklusive registrering, uppdatering, hämtning och borttagning.</p>
 * <p><strong>Funktioner:</strong></p>
 * <ul>
 * <li>Skapar, uppdaterar och hämtar kundinformation</li>
 * <li>Kopplar kunder till befintliga eller nya adresser</li>
 * <li>Validerar att kund och adress existerar</li>
 * <li>Förhindrar ogiltig borttagning av kundens enda adress</li>
 * </ul>
 * <p><strong>Teknisk roll:</strong> Spring {@code @Service} som samordnar repositories samt mappar mellan entiteter och kundrelaterade DTO:er.</p>
 * ──────────────────────────────────────────────
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final KeycloakUserService keycloakUserService;
    private final BookingRepository bookingRepository;

    private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository, KeycloakUserService keycloakUserService, BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.keycloakUserService = keycloakUserService;
        this.bookingRepository = bookingRepository;
    }

    public List<CustomerResponse> getAllCustomers() {
        logger.info("Hämtar alla kunder:");
        return customerRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        logger.info("Hämtar kund med ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + id + " hittades inte."));
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        logger.info("Skapar ny kund: {}", request.username());

        String keycloakId = keycloakUserService.createUserInKeycloak(
                request.email(),
                request.username(),
                request.password(),
                request.firstName(),
                request.lastName(),
                "USER"
        );

        try {
            Address address = addressRepository.findById(request.addressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Adress med ID " + request.addressId() + " hittades inte."));

            Customer customer = new Customer(
                    request.username(),
                    keycloakId,
                    request.firstName(),
                    request.lastName(),
                    address
            );

            logger.info("Kund {} skapad framgångsrikt.", customer.getUsername());
            return mapToResponse(customerRepository.save(customer));

        } catch (Exception e) {
            logger.error("Kunde inte spara kund i DB. Rullar tillbaka Keycloak-användare: {}", keycloakId, e);
            keycloakUserService.deleteUserInKeycloak(keycloakId);

            throw new RuntimeException("Ett fel uppstod vid kundregistrering. Försök igen.", e);
        }
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        logger.info("Uppdaterar kund med ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + id + " hittades inte."));

        Address newAddress = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Adress med ID " + request.addressId() + " hittades inte."));

        customer.setUsername(request.username());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setAddress(newAddress);

        logger.info("Kund {} uppdaterad framgångsrikt.", customer.getUsername());
        return mapToResponse(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        logger.warn("Raderar kund med ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kunde inte radera: Kund med ID " + id + " hittades inte."));

        List<Booking> bookings = bookingRepository.findByCustomerId(id);
        if (!bookings.isEmpty()) {
            logger.info("Raderar {} bokningar kopplade till kunden.", bookings.size());
            bookingRepository.deleteAll(bookings);
        }

        keycloakUserService.deleteUserInKeycloak(customer.getKeycloakId());

        customerRepository.delete(customer);

        logger.info("Kund {} raderad framgångsrikt.", id);
    }

    public CustomerResponse createAddressForCustomer(Long customerId, AddressRecord request) {
        logger.info("Lägger till ny adress till kund med ID: {}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte."));

        Address newAddress = new Address();
        newAddress.setStreetName(request.streetName());
        newAddress.setCity(request.city());
        newAddress.setZipCode(request.zipCode());
        newAddress.setCountry(request.country());
        Address savedAddress = addressRepository.save(newAddress);

        customer.setAddress(savedAddress);

        logger.info("Kund {} uppdaterad framgångsrikt.", customer.getUsername());
        return mapToResponse(customerRepository.save(customer));
    }

    public void deleteAddressFromCustomer(Long customerId, Long addressId) {
        logger.warn("Försöker radera adress med ID: {} för kund: {}", addressId, customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Kund med ID " + customerId + " hittades inte."));

        if (customer.getAddress() == null || !customer.getAddress().getId().equals(addressId)) {
            throw new IllegalArgumentException("Adressen tillhör inte denna kund.");
        }

        customer.setAddress(null);

        customerRepository.save(customer);

        logger.info("Adress {} raderad från kund {}", addressId, customerId);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        AddressRecord addressRecord = new AddressRecord(
                customer.getAddress().getId(),
                customer.getAddress().getStreetName(),
                customer.getAddress().getCity(),
                customer.getAddress().getZipCode(),
                customer.getAddress().getCountry()
        );

        return new CustomerResponse(
                customer.getId(),
                customer.getUsername(),
                customer.getFirstName(),
                customer.getLastName(),
                addressRecord
        );
    }
}