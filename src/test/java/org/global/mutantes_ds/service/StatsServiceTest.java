package org.global.mutantes_ds.service;

import org.global.mutantes_ds.dto.StatsResponse;
import org.global.mutantes_ds.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatsServiceTest {

    private DnaRecordRepository repository;
    private StatsService service;

    @BeforeEach
    void setUp() {
        repository = mock(DnaRecordRepository.class);
        service = new StatsService(repository);
    }

    // ============================================================
    // 1. Ratio normal
    // ============================================================

    @Test
    void shouldReturnCorrectStats() {
        when(repository.countByIsMutant(true)).thenReturn(40L);
        when(repository.countByIsMutant(false)).thenReturn(100L);

        StatsResponse response = service.getStats();

        assertEquals(40L, response.getCount_mutant_dna());
        assertEquals(100L, response.getCount_human_dna());
        assertEquals(0.4, response.getRatio());
    }

    // ============================================================
    // 2. Sin humanos (evitar división por cero)
    // ============================================================

    @Test
    void shouldHandleZeroHumans() {
        when(repository.countByIsMutant(true)).thenReturn(10L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse response = service.getStats();

        assertEquals(10L, response.getCount_mutant_dna());
        assertEquals(0L, response.getCount_human_dna());
        assertEquals(10.0, response.getRatio());
    }

    // ============================================================
    // 3. Sin mutantes
    // ============================================================

    @Test
    void shouldHandleZeroMutants() {
        when(repository.countByIsMutant(true)).thenReturn(0L);
        when(repository.countByIsMutant(false)).thenReturn(20L);

        StatsResponse response = service.getStats();

        assertEquals(0L, response.getCount_mutant_dna());
        assertEquals(20L, response.getCount_human_dna());
        assertEquals(0.0, response.getRatio());
    }

    // ============================================================
    // 4. Sin registros
    // ============================================================

    @Test
    void shouldHandleNoRecords() {
        when(repository.countByIsMutant(true)).thenReturn(0L);
        when(repository.countByIsMutant(false)).thenReturn(0L);

        StatsResponse response = service.getStats();

        assertEquals(0L, response.getCount_mutant_dna());
        assertEquals(0L, response.getCount_human_dna());
        assertEquals(0.0, response.getRatio());
    }

    // ============================================================
    // 5. Ratio decimal correcto
    // ============================================================

    @Test
    void shouldReturnDecimalRatio() {
        when(repository.countByIsMutant(true)).thenReturn(3L);
        when(repository.countByIsMutant(false)).thenReturn(7L);

        StatsResponse response = service.getStats();

        assertEquals(3L, response.getCount_mutant_dna());
        assertEquals(7L, response.getCount_human_dna());
        assertEquals(3.0 / 7.0, response.getRatio());
    }

    // ============================================================
    // 6. Grandes cantidades
    // ============================================================

    @Test
    void shouldHandleLargeNumbers() {
        when(repository.countByIsMutant(true)).thenReturn(1_000_000L);
        when(repository.countByIsMutant(false)).thenReturn(2_000_000L);

        StatsResponse response = service.getStats();

        assertEquals(1_000_000L, response.getCount_mutant_dna());
        assertEquals(2_000_000L, response.getCount_human_dna());
        assertEquals(0.5, response.getRatio());
    }
}
