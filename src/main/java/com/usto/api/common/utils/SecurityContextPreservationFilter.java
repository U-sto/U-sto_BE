package com.usto.api.common.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SecurityContext를 에러 상황에서도 절대 잃어버리지 않도록 보호하는 필터
 */
@Slf4j
@RequiredArgsConstructor
public class SecurityContextPreservationFilter implements Filter {

    private final SecurityContextRepository securityContextRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 1️⃣ 요청 시작 시 SecurityContext 백업
        SecurityContext contextBefore = SecurityContextHolder.getContext();
        HttpSession session = req.getSession(false);

        // 세션이 있으면 그 안의 SecurityContext도 백업
        SecurityContext contextInSession = null;
        if (session != null) {
            contextInSession = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        }

        try {
            // 2️⃣ 요청 처리
            chain.doFilter(request, response);

        } catch (Exception e) {
            // 3️⃣ 에러 발생! → SecurityContext 복원
            log.error("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.error("┃ ⚠️ 에러 발생했지만 SecurityContext 보호 중...");
            log.error("┃ 📋 에러: {}", e.getMessage());

            // 현재 SecurityContext가 없어졌는지 확인
            SecurityContext contextAfter = SecurityContextHolder.getContext();

            if (contextAfter == null || contextAfter.getAuthentication() == null) {
                log.warn("┃ ❌ SecurityContext가 사라짐! 복원 시작...");

                // 백업한 것 중 유효한 것을 복원
                SecurityContext toRestore = null;

                if (contextBefore != null && contextBefore.getAuthentication() != null) {
                    toRestore = contextBefore;
                    log.info("┃ ✅ 요청 시작 시의 SecurityContext 복원");
                } else if (contextInSession != null && contextInSession.getAuthentication() != null) {
                    toRestore = contextInSession;
                    log.info("┃ ✅ 세션의 SecurityContext 복원");
                }

                if (toRestore != null) {
                    // SecurityContextHolder에 복원
                    SecurityContextHolder.setContext(toRestore);

                    // 세션에도 강제 저장
                    if (session != null) {
                        session.setAttribute("SPRING_SECURITY_CONTEXT", toRestore);
                        log.info("┃    └─ 사용자: {}", toRestore.getAuthentication().getName());
                    }
                }
            } else {
                log.info("┃ ✅ SecurityContext 유지됨");
            }

            log.error("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // 에러는 그대로 전파 (GlobalExceptionHandler가 처리)
            throw e;

        } finally {
            // 4️⃣ 요청 종료 시 SecurityContext 최종 저장
            SecurityContext finalContext = SecurityContextHolder.getContext();

            if (finalContext != null && finalContext.getAuthentication() != null) {
                // Repository를 통한 저장
                securityContextRepository.saveContext(finalContext, req, res);

                // 세션에도 직접 저장 (이중 안전장치)
                HttpSession finalSession = req.getSession(false);
                if (finalSession != null) {
                    finalSession.setAttribute("SPRING_SECURITY_CONTEXT", finalContext);
                }

                log.debug("🔒 SecurityContext 최종 저장 완료: {}",
                        finalContext.getAuthentication().getName());
            }
        }
    }
}