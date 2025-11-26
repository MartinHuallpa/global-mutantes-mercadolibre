package org.global.mutantes_ds.service;

import lombok.RequiredArgsConstructor;
import org.global.mutantes_ds.dto.StatsResponse;
import org.global.mutantes_ds.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    public StatsResponse getStats() {

        long mutants = dnaRecordRepository.countByIsMutant(true);
        long humans = dnaRecordRepository.countByIsMutant(false);

        double ratio;

        if (humans == 0) {
            ratio = mutants > 0 ? (double) mutants : 0.0;
        } else {
            ratio = (double) mutants / humans;
        }

        return new StatsResponse(mutants, humans, ratio);
    }

    public StatsResponse getStats(LocalDateTime startDate, LocalDateTime endDate) {

        // Si faltan fechas, se retorna la estadística general
        if (startDate == null || endDate == null) {
            return getStats();
        }

        long mutants = dnaRecordRepository
                .countByIsMutantAndCreatedAtBetween(true, startDate, endDate);

        long humans = dnaRecordRepository
                .countByIsMutantAndCreatedAtBetween(false, startDate, endDate);

        double ratio;

        if (humans == 0) {
            ratio = mutants > 0 ? (double) mutants : 0.0;
        } else {
            ratio = (double) mutants / humans;
        }

        return new StatsResponse(mutants, humans, ratio);
    }
}
