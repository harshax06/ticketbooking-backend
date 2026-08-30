package com.harsha.ticketbooking.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getRequestURI().startsWith("/api/v1/bookings")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket()) ;

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"status\":429,\"error\":\"TOO_MANY_REQUESTS\",\"message\":\"Booking rate limit exceeded. Please slow down.\"}"
            );
        }
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))); // 5 requests/min
        return Bucket.builder().addLimit(limit).build();
    }

}
