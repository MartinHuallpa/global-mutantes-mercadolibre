package org.global.mutantes_ds.service;

import lombok.RequiredArgsConstructor;
import org.global.mutantes_ds.entity.DnaRecord;
import org.global.mutantes_ds.exception.DnaHashCalculationException;
import org.global.mutantes_ds.repository.DnaRecordRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    @Async
    @Transactional
    @CacheEvict(value = { "stats", "statsByDate" }, allEntries = true)
    public CompletableFuture<Boolean> analyzeDna(String[] dna) {

        String hash = calculateDnaHash(dna);

        boolean result = dnaRecordRepository.findByDnaHash(hash)
                .map(DnaRecord::isMutant)
                .orElseGet(() -> {

                    boolean isMutant = mutantDetector.isMutant(dna);

                    DnaRecord record = DnaRecord.builder()
                            .dnaHash(hash)
                            .isMutant(isMutant)
                            .createdAt(LocalDateTime.now())
                            .build();

                    dnaRecordRepository.save(record);

                    return isMutant;
                });

        // Devuelve el resultado envuelto en un Future para cumplir con el modo asíncrono
        return CompletableFuture.completedFuture(result);
    }

    public String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            for (String row : dna) {
                if (row != null) digest.update(row.getBytes());
            }

            byte[] hash = digest.digest();
            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new DnaHashCalculationException("Error al calcular hash del ADN", e);
        }
    }

    // Elimina un ADN previamente analizado utilizando su hash.
    // Si no existe el registro, se lanza una excepción para que el controller retorne 404.
    public void deleteByHash(String hash) {

        // Buscar el registro
        var record = dnaRecordRepository.findByDnaHash(hash);

        if (record.isEmpty()) {
            // No existe → se avisa al controller
            throw new IllegalArgumentException("El ADN con ese hash no existe");
        }

        // Eliminar el registro
        dnaRecordRepository.delete(record.get());
    }
}
