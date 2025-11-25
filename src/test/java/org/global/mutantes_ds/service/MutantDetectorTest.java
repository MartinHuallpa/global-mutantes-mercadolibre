package org.global.mutantes_ds.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
    }

    // ============================================================
    // 1. Mutantes reales
    // ============================================================

    @Test
    void shouldDetectHorizontalSequence() {
        String[] dna = {
                "AAAAAA",
                "CCCCCC",
                "TTATGT",
                "AGACGG",
                "GCATCA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    void shouldDetectVerticalSequence() {
        String[] dna = {
                "ATGC",
                "ATGC",
                "ATGC",
                "ATGC"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    void shouldDetectDiagonalDescendingSequence() {
        String[] dna = {
                "AAGCTA",
                "CAAGTC",
                "TTAAGT",
                "AGAAAG",
                "GCCCTA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    void shouldDetectDiagonalAscendingSequence() {
        String[] dna = {
                "AAAGGA",
                "CAGTGC",
                "TTAAGT",
                "AGACGG",
                "CCACTA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    // ============================================================
    // 2. Múltiples secuencias
    // ============================================================

    @Test
    void shouldDetectTwoHorizontalSequences() {
        String[] dna = {
                "AAAAAA",
                "CCCCCC",
                "TTATGT",
                "AGACGG",
                "GCATCA",
                "TCACTG"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    void shouldDetectHorizontalAndVertical() {
        String[] dna = {
                "AAAAAA",
                "AAGTGA",
                "ATTAGA",
                "AGACGA",
                "AGGTGA",
                "TCACTA"
        };
        assertTrue(detector.isMutant(dna));
    }

    // ============================================================
    // 3. Humanos (false)
    // ============================================================

    @Test
    void shouldReturnFalseForHumanComplexPattern() {
        String[] dna = {
                "ATCGTA",
                "CGATCG",
                "GTCAGT",
                "TCAGTC",
                "AGCTAC",
                "CTAGCA"
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    void shouldReturnFalseWithNoSequences() {
        String[] dna = {
                "ATGC",
                "CAGT",
                "TTAC",
                "AGTC"
        };
        assertFalse(detector.isMutant(dna));
    }

    // ============================================================
    // 4. Validaciones
    // ============================================================

    @Test
    void shouldReturnFalseForNullDna() {
        assertFalse(detector.isMutant(null));
    }

    @Test
    void shouldReturnFalseForEmptyArray() {
        String[] dna = {};
        assertFalse(detector.isMutant(dna));
    }

    @Test
    void shouldReturnFalseForNonSquareMatrix() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT"
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    void shouldReturnFalseForNullRow() {
        String[] dna = {
                "ATGCGA",
                null,
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    @Test
    void shouldReturnFalseForInvalidCharacters() {
        String[] dna = {
                "ATGCGA",
                "CAGTXC",
                "TTATGT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        };
        assertFalse(detector.isMutant(dna));
    }

    // ============================================================
    // 5. Tamaños y límites
    // ============================================================

    @Test
    void shouldDetectMutantInSmall4x4Matrix() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TTAT",
                "AGTC"
        };
        assertTrue(detector.isMutant(dna));
    }

    @Test
    void shouldDetectMutantInLargeMatrix() {
        String[] dna = {
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA",
                "CCCCTACCCC",
                "TCACTGTCAC",
                "ATGCGAATGC",
                "CAGTGCCAGT",
                "TTATGTTTAT",
                "AGAAGGATAA"
        };
        assertTrue(detector.isMutant(dna));
    }

    // ============================================================
    // 6. Early termination
    // ============================================================

    @Test
    void shouldStopEarlyWhenTwoSequencesFound() {
        String[] dna = {
                "AAAAAA",
                "AAAAAA",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };

        long start = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long end = System.nanoTime();

        assertTrue(result);
        assertTrue((end - start) < 10_000_000);
    }
}
