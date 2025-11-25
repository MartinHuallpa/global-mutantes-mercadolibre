package org.global.mutantes_ds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.global.mutantes_ds.validation.ValidDnaSequence;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request con la secuencia de ADN a analizar")
public class DnaRequest {

    @ValidDnaSequence
    @Schema(
            description = "Arreglo de strings que representa la matriz NxN del ADN",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]"
    )
    private String[] dna;
}
