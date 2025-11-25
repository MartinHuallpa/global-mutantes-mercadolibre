package org.global.mutantes_ds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dna_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hash SHA-256 del ADN. Debe ser único.
    @Column(name = "dna_hash", nullable = false, unique = true, length = 64)
    private String dnaHash;

    // True = mutante, False = humano.
    @Column(name = "is_mutant", nullable = false)
    private boolean isMutant;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
