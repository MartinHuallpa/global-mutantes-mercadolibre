package org.global.mutantes_ds.service;

import lombok.RequiredArgsConstructor;
import org.global.mutantes_ds.entity.DnaRecord;
import org.global.mutantes_ds.exception.DnaHashCalculationException;
import org.global.mutantes_ds.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    @Transactional
    public boolean analyzeDna(String[] dna) {

        String hash = calculateDnaHash(dna);

        // Si ya existe en DB → devolver resultado cacheado
        return dnaRecordRepository.findByDnaHash(hash)
                .map(DnaRecord::isMutant)
                .orElseGet(() -> {

                    // Ejecutar algoritmo
                    boolean isMutant = mutantDetector.isMutant(dna);

                    // Guardar registro
                    DnaRecord record = DnaRecord.builder()
                            .dnaHash(hash)
                            .isMutant(isMutant)
                            .createdAt(LocalDateTime.now()) // fecha de creación
                            .build();

                    dnaRecordRepository.save(record);

                    return isMutant;
                });
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
