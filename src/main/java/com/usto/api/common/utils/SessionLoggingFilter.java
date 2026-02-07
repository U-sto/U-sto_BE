package com.usto.api.common.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SessionLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        log.debug("=== Session Debug ===");
        log.debug("Request URI: {}", httpRequest.getRequestURI());
        log.debug("Session exists: {}", session != null);

        if (session != null) {
            log.debug("Session ID: {}", session.getId());
            log.debug("Session creation time: {}", session.getCreationTime());
            log.debug("Session last accessed: {}", session.getLastAccessedTime());
        }

        log.debug("SecurityContext authenticated: {}",
                SecurityContextHolder.getContext().getAuthentication() != null);

        chain.doFilter(request, response);
    }
}
