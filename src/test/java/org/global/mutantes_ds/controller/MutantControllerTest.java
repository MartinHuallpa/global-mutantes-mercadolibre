package org.global.mutantes_ds.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.global.mutantes_ds.dto.DnaRequest;
import org.global.mutantes_ds.dto.StatsResponse;
import org.global.mutantes_ds.service.MutantService;
import org.global.mutantes_ds.service.StatsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MutantService mutantService;

    @MockBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    // ============================================================
    // 1. MUTANTE → 200 OK
    // ============================================================

    @Test
    void shouldReturn200IfMutant() throws Exception {
        when(mutantService.analyzeDna(any())).thenReturn(true);

        DnaRequest req = new DnaRequest(new String[]{"AAAA", "TTTT", "CCCC", "GGGG"});

        mockMvc.perform(
                        post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isOk());
    }

    // ============================================================
    // 2. HUMANO → 403 Forbidden
    // ============================================================

    @Test
    void shouldReturn403IfHuman() throws Exception {
        when(mutantService.analyzeDna(any())).thenReturn(false);

        DnaRequest req = new DnaRequest(new String[]{"ATGC", "CAGT", "TTAT", "AGAC"});

        mockMvc.perform(
                        post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 3. ADN inválido → 400 Bad Request
    // ============================================================

    @Test
    void shouldReturn400ForInvalidDna() throws Exception {
        DnaRequest req = new DnaRequest(new String[]{"AXYZ", "CAGT"});

        mockMvc.perform(
                        post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 4. Error interno → 500
    // ============================================================

    @Test
    void shouldReturn500IfServiceThrowsException() throws Exception {
        when(mutantService.analyzeDna(any())).thenThrow(new RuntimeException("boom"));

        DnaRequest req = new DnaRequest(new String[]{"AAAA", "TTTT", "CCCC", "GGGG"});

        mockMvc.perform(
                        post("/mutant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                )
                .andExpect(status().isInternalServerError());
    }

    // ============================================================
    // 5. /stats retorna valores correctos
    // ============================================================

    @Test
    void shouldReturnStats() throws Exception {

        StatsResponse stats = new StatsResponse(40L, 100L, 0.4);
        when(statsService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(40))
                .andExpect(jsonPath("$.count_human_dna").value(100))
                .andExpect(jsonPath("$.ratio").value(0.4));
    }

    // ============================================================
    // 6. Verifica llamada al servicio
    // ============================================================

    @Test
    void shouldCallStatsService() throws Exception {
        when(statsService.getStats()).thenReturn(new StatsResponse(1, 1, 1));

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk());

        verify(statsService, times(1)).getStats();
    }

    // ============================================================
    // 7. JSON correcto
    // ============================================================

    @Test
    void shouldReturnValidJsonStructure() throws Exception {

        StatsResponse stats = new StatsResponse(10L, 5L, 2.0);
        when(statsService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").exists())
                .andExpect(jsonPath("$.count_human_dna").exists())
                .andExpect(jsonPath("$.ratio").exists());
    }

    // ============================================================
    // 8. /stats manejo de excepciones
    // ============================================================

    @Test
    void shouldReturn500IfStatsServiceFails() throws Exception {
        when(statsService.getStats()).thenThrow(new RuntimeException("fail"));

        mockMvc.perform(get("/stats"))
                .andExpect(status().isInternalServerError());
    }
}
