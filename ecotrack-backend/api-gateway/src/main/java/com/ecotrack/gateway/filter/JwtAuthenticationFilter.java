package com.ecotrack.gateway.filter;

import com.ecotrack.gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Validates the JWT token on every incoming request before it is routed to
 * a microservice.
 *
 * How it works:
 *  1. If the request path is public (login, register, swagger, etc.) or is a CORS preflight request (OPTIONS), it is
 *     allowed through immediately.
 *  2. Otherwise, the "Authorization: Bearer <token>" header is required.
 *  3. If the token is missing or invalid, the Gateway rejects the request
 *     with 401 Unauthorized before it ever reaches a microservice.
 *  4. If the token is valid, the user's id, email and role are extracted
 *     from it and forwarded to the downstream service as simple HTTP
 *     headers (X-User-Id, X-User-Email, X-User-Role). This means the
 *     downstream microservices trust the Gateway and do not need to
 *     re-parse the JWT themselves.
 */
@Component
public class JwtAuthenticationFilter implements WebFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/recycler-applications/apply",
            "/api/v1/industry-applications/apply",
            "/api/v1/users/regions",
            "/api/v1/regions",
            "/api/v1/waste-categories",
            "/api/v1/categories",
            "/api-docs",
            "/swagger-ui"
    );

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public int getOrder() {
        return -1; // run before routing
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (CorsUtils.isPreFlightRequest(request) || isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return rejectRequest(exchange, "Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return rejectRequest(exchange, "Invalid or expired token");
        }

        Claims claims = jwtUtil.parseClaims(token);
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(claims.get("userId")))
                .header("X-User-Email", claims.getSubject())
                .header("X-User-Role", String.valueOf(claims.get("role")))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format(
                "{\"status\":401,\"message\":\"%s\",\"data\":null}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
