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

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

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

        String[] dna = request.getDna();

        // Validación básica del array recibido
        if (dna == null || dna.length == 0) {
            return ResponseEntity.badRequest().build();
        }

        // Validación de tamaño máximo permitido (1000x1000)
        // Evita procesar matrices excesivamente grandes
        if (dna.length > 1000 || dna[0].length() > 1000) {
            return ResponseEntity.badRequest().build();
        }

        CompletableFuture<Boolean> future = mutantService.analyzeDna(dna);
        boolean isMutant = future.join(); // obtiene el resultado del procesamiento asíncrono

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
            description = "Permite obtener estadísticas completas o filtradas por rango de fecha.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Estadísticas devueltas correctamente",
                            content = @Content(schema = @Schema(implementation = StatsResponse.class))
                    )
            }
    )
    public ResponseEntity<StatsResponse> getStats(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {

        // Si no viene rango, retorna estadísticas completas
        if (startDate == null || endDate == null) {
            return ResponseEntity.ok(statsService.getStats());
        }

        // Estadísticas filtradas por fecha
        return ResponseEntity.ok(statsService.getStats(startDate, endDate));
    }

    // -------------------------------------------------------------
    // DELETE /mutant/{hash} → elimina un registro de ADN
    // -------------------------------------------------------------
    @DeleteMapping("/mutant/{hash}")
    @Operation(
            summary = "Elimina un ADN analizado por su hash",
            description = "Permite eliminar un registro previamente guardado en la base de datos.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "No existe un ADN con ese hash")
            }
    )
    public ResponseEntity<Void> deleteMutant(@PathVariable String hash) {

        try {
            mutantService.deleteByHash(hash);
            return ResponseEntity.noContent().build(); // 204
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }
}
