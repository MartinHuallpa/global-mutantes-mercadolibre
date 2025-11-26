package org.global.mutantes_ds.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class MutantDetector {

    private static final int SEQUENCE = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    public boolean isMutant(String[] dna) {

        if (dna == null || dna.length == 0)
            return false;

        // Validación de tamaño máximo
        if (dna.length > 1000)
            return false;

        int n = dna.length;

        char[][] matrix = new char[n][n];

        for (int i = 0; i < n; i++) {

            String row = dna[i];

            if (row == null || row.length() != n)
                return false;

            // Segunda validación de tamaño (columnas)
            if (row.length() > 1000)
                return false;

            for (char c : row.toCharArray()) {
                if (!VALID_BASES.contains(c))
                    return false;
            }

            matrix[i] = row.toCharArray();
        }

        int sequences = 0;

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                char base = matrix[row][col];

                // →
                if (col <= n - SEQUENCE &&
                        check(matrix, row, col, 0, 1, base)) {
                    sequences++;
                }

                // ↓
                if (row <= n - SEQUENCE &&
                        check(matrix, row, col, 1, 0, base)) {
                    sequences++;
                }

                // ↘
                if (row <= n - SEQUENCE && col <= n - SEQUENCE &&
                        check(matrix, row, col, 1, 1, base)) {
                    sequences++;
                }

                // ↗
                if (row >= SEQUENCE - 1 && col <= n - SEQUENCE &&
                        check(matrix, row, col, -1, 1, base)) {
                    sequences++;
                }

                if (sequences > 1)
                    return true;
            }
        }

        return false;
    }

    private boolean check(char[][] m, int row, int col,
                          int dRow, int dCol, char base) {

        for (int i = 1; i < SEQUENCE; i++) {
            int r = row + i * dRow;
            int c = col + i * dCol;

            if (m[r][c] != base)
                return false;
        }

        // Secuencia encontrada: loggeo dirección y ubicación
        log.debug("Secuencia encontrada con dirección ({}, {}) iniciando en ({}, {})",
                dRow, dCol, row, col);

        return true;
    }
}
