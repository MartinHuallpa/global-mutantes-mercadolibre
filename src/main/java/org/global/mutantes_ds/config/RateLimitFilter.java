package org.global.mutantes_ds.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends HttpFilter {

    // requests por minuto permitidas
    private static final int LIMIT = 10;
    private static final long WINDOW_MILLIS = 60_000;

    // ip → timestamps de requests
    private final Map<String, List<Long>> requestTimes = new ConcurrentHashMap<>();

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        String ip = request.getRemoteAddr();
        long now = Instant.now().toEpochMilli();

        requestTimes.putIfAbsent(ip, new ArrayList<>());
        List<Long> timestamps = requestTimes.get(ip);

        // Limpiar requests fuera de la ventana de 1 minuto
        timestamps.removeIf(t -> t < now - WINDOW_MILLIS);

        if (timestamps.size() >= LIMIT) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded. Max 10 requests per minute.");
            return;
        }

        // Registrar nueva request
        timestamps.add(now);

        chain.doFilter(request, response);
    }
}
