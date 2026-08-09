package com.ecotrack.notification.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String headerUserId = request.getHeader("X-User-Id");
        String headerEmail = request.getHeader("X-User-Email");
        String headerRole = request.getHeader("X-User-Role");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.parseClaims(token);
                Object rawUserId = claims.get("userId");
                Long userId = null;
                if (rawUserId != null && !rawUserId.toString().equals("null")) {
                    try {
                        userId = Long.valueOf(rawUserId.toString());
                    } catch (NumberFormatException ignored) {}
                }

                String email = claims.getSubject();
                String rawRole = String.valueOf(claims.get("role"));
                String cleanRole = rawRole.startsWith("ROLE_") ? rawRole.substring(5) : rawRole;

                AuthenticatedUser principal = new AuthenticatedUser(userId, email, cleanRole);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + cleanRole)));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (headerEmail != null && headerRole != null) {
            Long userId = null;
            if (headerUserId != null && !headerUserId.equals("null")) {
                try {
                    userId = Long.valueOf(headerUserId);
                } catch (NumberFormatException ignored) {}
            }
            String cleanRole = headerRole.startsWith("ROLE_") ? headerRole.substring(5) : headerRole;

            AuthenticatedUser principal = new AuthenticatedUser(userId, headerEmail, cleanRole);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + cleanRole)));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
