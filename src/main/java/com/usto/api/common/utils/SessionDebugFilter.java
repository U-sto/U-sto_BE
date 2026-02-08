package com.usto.api.common.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class SessionDebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // Swagger 제외
        if (uri.startsWith("/swagger") || uri.startsWith("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String cookie = req.getHeader("Cookie");
        HttpSession session = req.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String sessionStatus = session != null ?
                String.format("세션O(ID:%s)", session.getId().length() > 8 ? session.getId().substring(0, 8) : session.getId()) : "세션X";

        String authStatus = (auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal())) ?
                String.format("인증O(%s)", auth.getName()) : "인증X";

        String cookieStatus = (cookie != null && cookie.contains("JSESSIONID")) ? "쿠키O" : "쿠키X";

        log.info("▶ {} {} | {} {} {} → ",
                req.getMethod(), uri, cookieStatus, sessionStatus, authStatus);

        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;

        String statusEmoji = res.getStatus() < 300 ? "✅" :
                res.getStatus() < 400 ? "🔄" : "❌";

        log.info("◀ {} {} ({}ms)", statusEmoji, res.getStatus(), duration);
    }
}