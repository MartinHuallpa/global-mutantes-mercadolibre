package org.global.mutantes_ds.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.global.mutantes_ds.dto.DnaRequest;
import org.global.mutantes_ds.dto.StatsResponse;
import org.global.mutantes_ds.service.MutantService;
import org.global.mutantes_ds.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Mutant Detector API", description = "Endpoints del examen de MercadoLibre para detectar mutantes")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    // -------------------------------------------------------------
    // POST /mutant → retorna 200 si es mutante, 403 si no lo es
    // -------------------------------------------------------------
    @PostMapping("/mutant")
    @Operation(
            summary = "Determina si un ADN pertenece a un mutante",
            description = "Recibe un array NxN de bases nitrogenadas y aplica el algoritmo MutantDetector.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Es mutante"),
                    @ApiResponse(responseCode = "403", description = "No es mutante"),
                    @ApiResponse(responseCode = "400", description = "Request inválido",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Void> checkMutant(@Valid @RequestBody DnaRequest request) {

        boolean isMutant = mutantService.analyzeDna(request.getDna());

        // 200 → mutante
        // 403 → humano
        return isMutant
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // -------------------------------------------------------------
    // GET /stats → retorna estadísticas
    // -------------------------------------------------------------
    @GetMapping("/stats")
    @Operation(
            summary = "Devuelve estadísticas sobre los ADN analizados",
            description = "Muestra la cantidad de mutantes, humanos y el ratio entre ambos.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estadísticas devueltas correctamente",
                            content = @Content(schema = @Schema(implementation = StatsResponse.class))
                    )
            }
    )
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }
}
