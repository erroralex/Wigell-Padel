package com.nilsson.padel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nilsson.padel.dto.BookingRequest;
import com.nilsson.padel.dto.BookingResponse;
import com.nilsson.padel.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * <h2>BookingControllerTest</h2>
 * <p>Integrationstestklass för {@link BookingController}.</p>
 * <p>Denna klass testar REST-API-slutpunkterna för bokningshantering
 * genom att simulera HTTP-förfrågningar och verifiera kontrollerns beteende
 * samt integrationen med {@link BookingService}.</p>
 *
 * <h3>Funktioner som testas:</h3>
 * <ul>
 *     <li>Skapande av en ny bokning via POST-begäran, inklusive verifiering av HTTP-status (201 Created),
 *         Location-header och det returnerade bokningsobjektet.</li>
 *     <li>Simulering av autentiserade användare med specifika roller för att testa åtkomstkontroll.</li>
 * </ul>
 */
@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @WithMockUser(roles = "USER")
    void createBooking_ShouldReturn201AndLocationHeader() throws Exception {

        BookingRequest request = new BookingRequest(1L, 1L, LocalDate.of(2026, 4, 15), LocalTime.of(18, 0), 4);

        BookingResponse mockResponse = new BookingResponse(
                99L, 1L, 1L, "Court 1", LocalDate.of(2026, 4, 15),
                LocalTime.of(18, 0), 4, new BigDecimal("400.00"), new BigDecimal("35.50")
        );

        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/bookings/99"))
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.totalPriceSek").value(400.00));
    }
}