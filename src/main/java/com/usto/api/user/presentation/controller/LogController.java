package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.presentation.dto.request.LoginRequestDto;
import com.usto.api.user.presentation.dto.response.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            @Valid @RequestBody LoginRequestDto request,
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

        // 3) 응답용 사용자 정보 로딩 -> 승인 상태 확인
        User user = loginApplication.login(request.getUsrId());

        return ApiResponse.ok(
                "로그인 성공",
                new LoginResponseDto(
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
    public void logoutDoc() {
        // 실제 로직 없음 (SecurityConfig LogoutFilter가 처리)
    }
}
