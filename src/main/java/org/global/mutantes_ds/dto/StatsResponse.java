package org.global.mutantes_ds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta con estadísticas de ADN analizados")
public class StatsResponse {

    @Schema(description = "Cantidad de ADN mutantes detectados", example = "40")
    private long count_mutant_dna;

    @Schema(description = "Cantidad de ADN humanos detectados", example = "100")
    private long count_human_dna;

    @Schema(description = "Proporción entre mutantes y humanos", example = "0.4")
    private double ratio;
}
