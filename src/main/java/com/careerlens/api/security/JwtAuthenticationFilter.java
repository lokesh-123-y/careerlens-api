package com.careerlens.api.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.careerlens.api.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "JWT FILTER - Authorization Header: " + authHeader
        );

        // No JWT token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("JWT FILTER - No Bearer token");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Remove "Bearer " from token
            String token = authHeader.substring(7);

            // Extract email from JWT
            String email = jwtService.extractEmail(token);

            System.out.println(
                    "JWT FILTER - Email: " + email
            );

            // Set authentication if user is not already authenticated
            if (email != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "JWT FILTER - Authentication SUCCESS"
                );

                System.out.println(
                        "JWT FILTER - Is Authenticated: "
                                + authentication.isAuthenticated()
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT FILTER - Invalid token: "
                            + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}