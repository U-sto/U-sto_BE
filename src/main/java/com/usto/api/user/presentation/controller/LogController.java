package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.LoginApplication;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.presentation.dto.request.LoginRequestDto;
import com.usto.api.user.presentation.dto.response.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "log-controller", description = "로그인/아웃 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LogController {

    private final AuthenticationManager authenticationManager; //세션 토큰 관리자님
    private final LoginApplication loginApplication;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ApiResponse<?> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        // 비밀번호 검증 + 상태체크
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsrId(), request.getPwd())
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 세션에 저장
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        LoginUser loginUser = loginApplication.login(
                request.getUsrId()
        );

        return ApiResponse.ok(
                "로그인 성공",
                new LoginResponseDto(
                        loginUser.getUsrId(),
                        loginUser.getUsrNm()
                )
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public void logoutDoc() {
        // 실제 로직 없음 (SecurityConfig LogoutFilter가 처리)
    }
}
