package org.global.mutantes_ds.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends HttpFilter {

    // Máximo de requests por minuto
    private static final int LIMIT = 10;
    private static final long WINDOW_MILLIS = 60_000;

    // Almacena timestamps por IP
    private final Map<String, List<Long>> requestTimes = new ConcurrentHashMap<>();

    // Permite desactivar el rate limiter en tests:
    @Value("${rate-limiter.enabled:true}")
    private boolean rateLimiterEnabled;

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        // Si el rate limiting está desactivado → continuar sin verificar nada
        if (!rateLimiterEnabled) {
            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // Excluir rutas internas de desarrollo (no se rate-limitan)
        if (path.startsWith("/h2-console") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")) {

            chain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        requestTimes.putIfAbsent(ip, new ArrayList<>());
        List<Long> timestamps = requestTimes.get(ip);

        // Limpiar timestamps fuera de la ventana
        timestamps.removeIf(t -> t < now - WINDOW_MILLIS);

        // Si se excede el límite → 429
        if (timestamps.size() >= LIMIT) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Max 10 requests per minute.");
            return;
        }

        // Registrar esta request
        timestamps.add(now);

        chain.doFilter(request, response);
    }
}
