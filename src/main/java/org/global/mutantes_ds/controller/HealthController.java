package org.global.mutantes_ds.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {

        // Respuesta simple con estado UP y timestamp actual
        Map<String, Object> body = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(body);
    }
}
