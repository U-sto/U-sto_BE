package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.presentation.dto.request.LoginRequest;
import com.usto.api.user.presentation.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "log-controller", description = "로그인/아웃 관련 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogController {

    private final AuthenticationManager authenticationManager; //세션 토큰 관리자님
    private final LoginApplication loginApplication;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ApiResponse<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        // 1) 스프링 시큐리티 표준 인증(비번 틀리면 BadCredentialsException 발생)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsrId(), request.getPwd())
        );

        // 2) 인증 성공 시 SecurityContext 생성 + 세션 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            log.info(" 로그인 성공: {} (세션 ID: {})",
                    authentication.getName(), session.getId());
        }

        // 3) 응답용 사용자 정보 로딩 -> 승인 상태 확인
        User user = loginApplication.login(request.getUsrId());

        return ApiResponse.ok(
                "로그인 성공",
                new LoginResponse(
                        user.getUsrId(),
                        user.getUsrNm()
                )
        );

        //로그인 실패는 Controller에서 먹어버리는게 나을거같음
        /*
            [다른  API에서 로그인 사용자 정보 가져오는 방법]
            UserPrincipal me = (UserPrincipal) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
         */

        /*
                [최신 로그인 로직]

                POST /api/auth/login
                   ↓
                AuthenticationManager.authenticate()      // 아이디·비번 검증
                   ↓ (성공)
                SecurityContextRepository.saveContext()   // 세션 저장
                   ↓
                LoginApplication.loadLoginUser()           // 승인/계정상태 검증
                   ↓
                LoginResponse 반환
         */

    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ApiResponse<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
            ) {
        // 1️ 세션 정보 확인 (로깅용)
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");

            log.info("┃  세션 ID: {}", sessionId);
            log.info("┃  SecurityContext: {}", securityContext != null ? "있음" : "없음");

            // 2️⃣ 세션 무효화 (완전 삭제)
            try {
                session.invalidate();
                log.info("┃  세션 무효화 완료");
            } catch (IllegalStateException e) {
                log.warn("┃ ️ 이미 무효화된 세션");
            }
        } else {
            log.info("┃  세션 없음 (이미 로그아웃 상태)");
        }

        // 3️⃣ SecurityContext 제거
        SecurityContextHolder.clearContext();
        log.info("┃  SecurityContext 제거 완료");

        // 4️⃣ 쿠키 삭제 (명시적)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        log.info("┃  JSESSIONID 쿠키 삭제 완료");

        return ApiResponse.ok("로그아웃 성공");
    }
}
