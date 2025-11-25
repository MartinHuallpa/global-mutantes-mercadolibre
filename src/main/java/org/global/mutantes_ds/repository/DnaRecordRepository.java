package org.global.mutantes_ds.repository;

import org.global.mutantes_ds.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    Optional<DnaRecord> findByDnaHash(String dnaHash);

    long countByIsMutant(boolean isMutant);
}
