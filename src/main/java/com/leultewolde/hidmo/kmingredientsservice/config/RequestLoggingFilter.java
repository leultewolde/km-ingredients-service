package com.leultewolde.hidmo.kmingredientsservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that logs basic information about every incoming HTTP request.
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String query = request.getQueryString();
        String uri = request.getRequestURI();
        if (query != null && !query.isBlank()) {
            uri = uri + "?" + query;
        }
        log.info("Incoming request: {} {}", request.getMethod(), uri);
        log.info("Response: {} {}", response.getStatus(), response.getContentType());
        filterChain.doFilter(request, response);
    }
}
