package com.nilsson.padel.controller;

import com.nilsson.padel.service.CourtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h2>CourtControllerTest</h2>
 * <p>Integrationstestklass för {@link CourtController}.</p>
 * <p>Denna klass testar REST-API-slutpunkterna för hantering av padelbanor
 * genom att simulera HTTP-förfrågningar och verifiera kontrollerns beteende
 * samt säkerhets- och åtkomstkontroller för olika användarroller.</p>
 *
 * <h3>Funktioner som testas:</h3>
 * <ul>
 *     <li>Åtkomstkontroll för att hämta alla banor ({@code getAllCourts}) för en vanlig användare (förväntas 403 Forbidden).</li>
 *     <li>Borttagning av en bana ({@code deleteCourt}) av en administratör (förväntas 204 No Content).</li>
 *     <li>Simulering av autentiserade användare med specifika roller (USER, ADMIN) för att testa behörigheter.</li>
 * </ul>
 */
@WebMvcTest(CourtController.class)
@AutoConfigureMockMvc
class CourtControllerTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class SecurityTestConfig {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourtService courtService;

    @Test
    @WithMockUser(roles = "USER")
    void getAllCourts_ShouldReturn403Forbidden_ForNormalUser() throws Exception {
        mockMvc.perform(get("/api/v1/courts"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourt_ShouldReturn204NoContent_ForAdmin() throws Exception {
        Long courtId = 1L;

        doNothing().when(courtService).deleteCourt(courtId);

        mockMvc.perform(delete("/api/v1/courts/{id}", courtId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}