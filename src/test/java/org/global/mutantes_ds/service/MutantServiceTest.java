package org.global.mutantes_ds.service;

import org.global.mutantes_ds.entity.DnaRecord;
import org.global.mutantes_ds.repository.DnaRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MutantServiceTest {

    private MutantDetector detector;
    private DnaRecordRepository repository;
    private MutantService service;

    @BeforeEach
    void setUp() {
        detector = mock(MutantDetector.class);
        repository = mock(DnaRecordRepository.class);
        service = new MutantService(detector, repository);
    }

    // ============================================================
    // 1. Guarda mutante nuevo
    // ============================================================

    @Test
    void shouldSaveNewMutantDna() {
        String[] dna = {"AAAA", "TTTT", "CCCC", "GGGG"};

        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(dna)).thenReturn(true);

        boolean result = service.analyzeDna(dna);

        assertTrue(result);

        verify(repository).save(any(DnaRecord.class));
    }

    // ============================================================
    // 2. Guarda humano nuevo
    // ============================================================

    @Test
    void shouldSaveNewHumanDna() {
        String[] dna = {"ATCG", "TAGC", "CGTA", "GCAT"};

        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(dna)).thenReturn(false);

        boolean result = service.analyzeDna(dna);

        assertFalse(result);
        verify(repository).save(any(DnaRecord.class));
    }

    // ============================================================
    // 3. Usa cache si ADN ya existe
    // ============================================================

    @Test
    void shouldReturnCachedResultIfDnaExists() {

        String hash = "abc123";

        DnaRecord record = DnaRecord.builder()
                .dnaHash(hash)
                .isMutant(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findByDnaHash(hash)).thenReturn(Optional.of(record));
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.of(record));

        boolean result = service.analyzeDna(new String[]{"AAAA"});

        assertTrue(result);

        verify(detector, never()).isMutant(any());
        verify(repository, never()).save(any());
    }

    // ============================================================
    // 4. Hash consistente
    // ============================================================

    @Test
    void shouldGenerateConsistentHashForSameDna() {
        String[] dna = {"ATGC", "CAGT", "TTAT", "AGAC"};

        String hash1 = service.calculateDnaHash(dna);
        String hash2 = service.calculateDnaHash(dna);

        assertEquals(hash1, hash2);
    }

    // ============================================================
    // 5. Guarda hash correcto
    // ============================================================

    @Test
    void shouldSaveRecordWithCorrectHash() {
        String[] dna = {"ATGC", "CAGT", "TTAT", "AGAC"};

        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(dna)).thenReturn(false);

        service.analyzeDna(dna);

        ArgumentCaptor<DnaRecord> captor = ArgumentCaptor.forClass(DnaRecord.class);
        verify(repository).save(captor.capture());

        DnaRecord saved = captor.getValue();

        assertNotNull(saved.getDnaHash());
        assertEquals(saved.getDnaHash(), service.calculateDnaHash(dna));
        assertNotNull(saved.getCreatedAt());
    }
}
