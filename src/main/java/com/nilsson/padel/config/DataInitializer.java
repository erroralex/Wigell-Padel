package com.nilsson.padel.config;

import com.nilsson.padel.entity.Address;
import com.nilsson.padel.entity.Booking;
import com.nilsson.padel.entity.Court;
import com.nilsson.padel.entity.Customer;
import com.nilsson.padel.repository.AddressRepository;
import com.nilsson.padel.repository.BookingRepository;
import com.nilsson.padel.repository.CourtRepository;
import com.nilsson.padel.repository.CustomerRepository;
import com.nilsson.padel.security.KeycloakUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            CourtRepository courtRepository,
            BookingRepository bookingRepository,
            KeycloakUserService keycloakUserService) {

        return args -> {
            if (customerRepository.count() == 0) {
                logger.info("Initierar testdata för Wigell Padel...");

                Address addr1 = addressRepository.save(new Address("Padelgatan 1", "Stockholm", "11122", "Sverige"));
                Address addr2 = addressRepository.save(new Address("Tennisvägen 5", "Göteborg", "41233", "Sverige"));
                Address addr3 = addressRepository.save(new Address("Sportgränd 9", "Malmö", "21144", "Sverige"));

                try {
                    String adminId = keycloakUserService.createUserInKeycloak("admin@wigell.se", "admin", "admin123", "Boss", "Wigell", "ADMIN");
                    Customer adminCustomer = new Customer("admin", adminId, "Boss", "Wigell", addr1);

                    String annaId = keycloakUserService.createUserInKeycloak("anna@padel.se", "anna99", "password", "Anna", "Andersson", "USER");
                    Customer annaCustomer = new Customer("anna99", annaId, "Anna", "Andersson", addr1);

                    String bjornId = keycloakUserService.createUserInKeycloak("bjorn@padel.se", "bjorn_p", "password", "Björn", "Borg", "USER");
                    Customer bjornCustomer = new Customer("bjorn_p", bjornId, "Björn", "Borg", addr2);

                    String ceciliaId = keycloakUserService.createUserInKeycloak("cecilia@padel.se", "cecilia", "password", "Cecilia", "Lind", "USER");
                    Customer ceciliaCustomer = new Customer("cecilia", ceciliaId, "Cecilia", "Lind", addr3);

                    String davidId = keycloakUserService.createUserInKeycloak("david@padel.se", "david_d", "password", "David", "Dalin", "USER");
                    Customer davidCustomer = new Customer("david_d", davidId, "David", "Dalin", addr2);

                    customerRepository.saveAll(List.of(adminCustomer, annaCustomer, bjornCustomer, ceciliaCustomer, davidCustomer));
                    logger.info("5 kunder (inkl. Keycloak-identiteter) har skapats.");

                    Court c1 = new Court("Court 1 - Panorama", "Inomhusbana med panoramaglas", true, new BigDecimal("400.00"));
                    Court c2 = new Court("Court 2 - Standard", "Standard inomhusbana", true, new BigDecimal("350.00"));
                    Court c3 = new Court("Court 3 - Standard", "Standard inomhusbana", true, new BigDecimal("350.00"));
                    Court c4 = new Court("Court 4 - Outdoor", "Utomhusbana utan tak", false, new BigDecimal("250.00"));
                    Court c5 = new Court("Court 5 - Singel", "Mindre bana för singelspel (2 pers)", true, new BigDecimal("300.00"));

                    courtRepository.saveAll(List.of(c1, c2, c3, c4, c5));
                    logger.info("5 padelbanor har skapats.");

                    Booking b1 = new Booking(annaCustomer, c1, LocalDate.of(2026, 4, 14), LocalTime.of(18, 0), 4, new BigDecimal("400.00"));
                    b1.setTotalPriceEur(new BigDecimal("36.50"));

                    Booking b2 = new Booking(bjornCustomer, c4, LocalDate.of(2026, 4, 15), LocalTime.of(10, 0), 4, new BigDecimal("250.00"));
                    b2.setTotalPriceEur(new BigDecimal("22.80"));

                    bookingRepository.saveAll(List.of(b1, b2));
                    logger.info("2 bokningar har skapats.");

                } catch (Exception e) {
                    logger.error("Kunde inte initiera data. Ett fel uppstod: ", e);
                }
            } else {
                logger.info("Databasen innehåller redan data. DataInitializer hoppas över.");
            }
        };
    }
}