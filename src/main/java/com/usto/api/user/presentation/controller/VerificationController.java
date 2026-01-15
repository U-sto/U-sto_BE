package com.usto.api.user.presentation.controller;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.*;
import com.usto.api.user.domain.model.VerificationPurpose;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.request.EmailSendRequestDto;
import com.usto.api.user.presentation.dto.request.EmailVerifyRequestDto;
import com.usto.api.user.presentation.dto.request.SmsSendRequestDto;
import com.usto.api.user.presentation.dto.request.SmsVerifyRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "verification-controller", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final EmailVerificationApplication emailVerifyApplication;
    private final EmailSendApplication emailSendApplication;

    private final SmsVerificationApplication smsVerifyApplication;
    private final SmsSendApplication smsSendApplication;

    //회원가입
    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증번호 전송")
    public ApiResponse<?> sendEmail(
            @Valid @RequestBody EmailSendRequestDto request,
            HttpServletRequest http,
            HttpSession session

    )
    {

        VerificationPurpose purpose = VerificationPurpose.determinePurpose(
                request.getUsrId(),
                request.getUsrNm()
        );

        if(purpose.equals(VerificationPurpose.SIGNUP)){
            Boolean isExistsEmail = (Boolean) session.getAttribute("exists.auth.email.exists");
            String checkedEmail = (String) session.getAttribute(("exists.auth.email.target"));
            if(isExistsEmail == null || !checkedEmail.equals(request.getEmail())){
                throw new BusinessException("이메일 중복확인이 필요합니다.");
            }
        }

        if(purpose.equals(VerificationPurpose.FIND_ID)){
            session.setAttribute("email.pending.usrNm", request.getUsrNm());

        }
        if(purpose.equals(VerificationPurpose.RESET_PASSWORD)){
            session.setAttribute("email.pending.usrId", request.getUsrId());

        }

        session.setAttribute("email.pending.purpose", purpose);
        session.setAttribute("email.pending.target", request.getEmail());
        session.setAttribute("email.pending.sentAt", LocalDateTime.now());

        String actor = resolveActor(http);
        emailSendApplication.sendCodeToEmail(
                request,
                purpose,
                actor);

        session.removeAttribute("exists.auth.email.exists");

        return ApiResponse.ok("인증번호가 발송되었습니다.");
    }


    @PostMapping("/email/check")
    @Operation(summary = "이메일 인증번호 확인")
    public ApiResponse<?> verifyEmail(
            @Valid @RequestBody EmailVerifyRequestDto request,
            HttpSession session

    ) {

        VerificationPurpose purpose = (VerificationPurpose) session.getAttribute("email.pending.purpose");

        if (purpose == null) {
            throw new BusinessException("인증번호 발송 내역이 없습니다. no_purpose");
        }

        String target = (String) session.getAttribute("email.pending.target");
        if (target == null) {
            throw new BusinessException("인증번호 발송 내역이 없습니다. no_target");
        }

        emailVerifyApplication.verifyCode(request.getCode(),target,purpose);

        String sessionPrefix = getSessionPrefix(purpose);

        session.removeAttribute("email.pending.purpose");
        session.removeAttribute("email.pending.target");
        session.removeAttribute("email.pending.sentAt");

        switch (purpose){
            case FIND_ID -> {
                String usrNm = (String) session.getAttribute("email.pending.usrNm");
                session.setAttribute(sessionPrefix + ".auth.usrNm", usrNm);
                session.setAttribute(sessionPrefix + ".auth.email", target);
                session.removeAttribute("email.pending.usrNm");
                break;
            }
            case RESET_PASSWORD -> {
                String usrId = (String) session.getAttribute("email.pending.usrId");
                session.setAttribute(sessionPrefix + ".auth.usrId", usrId);
                session.setAttribute(sessionPrefix + ".auth.email", target);
                session.removeAttribute("email.pending.usrId");
                break;
            }
            case SIGNUP -> {
                session.setAttribute(sessionPrefix + ".auth.email", target);
                break;}
        }

        session.setAttribute(sessionPrefix +".auth.expiresAt", LocalDateTime.now().plusMinutes(15));

        return ApiResponse.ok("이메일 인증이 완료되었습니다.");

    }

    @PostMapping("/sms/send")
    @Operation(summary = "휴대폰 인증번호 전송")
    public ApiResponse<?> sendSms(
            @Valid @RequestBody SmsSendRequestDto request,
            HttpServletRequest http,
            HttpSession session

    ) {

        Boolean isExistsSms = (Boolean) session.getAttribute("exists.auth.sms.exists");
        String checkedSms = (String) session.getAttribute(("exists.auth.sms.target"));
        if(isExistsSms == null || !checkedSms.equals(request.getTarget())){
            throw new BusinessException("전화번호 중복확인이 필요합니다.");
        }

        VerificationPurpose purpose = VerificationPurpose.SIGNUP; // 전화번호 인증은 회원가입에서만 쓰임

        log.debug("[SIGNUP] 전화번호 중복 확인 완료: {}", request.getTarget());

        session.setAttribute("sms.pending.target", request.getTarget());
        session.setAttribute("sms.pending.sentAt", LocalDateTime.now());

        String actor = resolveActor(http);

        smsSendApplication.sendCodeToSms(
                request,
                purpose,
                actor);

        return ApiResponse.ok("인증번호가 문자로 발송되었습니다.");
    }

    @PostMapping("/sms/check")
    @Operation(summary = "휴대폰 인증번호 확인")
    public ResponseEntity<String> verifyCode(
            @Valid @RequestBody
            SmsVerifyRequestDto request,
            HttpSession session
    )
    {

        VerificationPurpose purpose = VerificationPurpose.SIGNUP; // 전화번호 인증은 회원가입에서만 쓰임

        String target = (String) session.getAttribute("sms.pending.target");

        smsVerifyApplication.verifyCode(request.getCode(),target,purpose);

        session.removeAttribute("sms.pending.target");
        session.removeAttribute("sms.pending.sentAt");

        String sessionPrefix = getSessionPrefix(purpose);

        session.setAttribute( sessionPrefix +".auth.sms", target);
        session.setAttribute(sessionPrefix +".auth.expiresAt", LocalDateTime.now().plusMinutes(15));

        session.removeAttribute("exists.auth.sms");
        session.removeAttribute("exists.auth.sms.exists");
        session.removeAttribute("exists.auth.sms.target");

        return ResponseEntity.ok("전화번호 인증이 완료되었습니다.");
    }


    private String resolveActor(HttpServletRequest http) {
        // 로그인 기반이면 여기서 SecurityContext에서 usrId 꺼냄
        // 아니면 IP+UA
        String ip = http.getRemoteAddr();
        String ua = http.getHeader("User-Agent");
        return "ANON ip=" + ip + " ua=" + (ua == null ? "-" : ua);
    }

    private String getSessionPrefix(VerificationPurpose purpose) {
        return switch (purpose) {
            case SIGNUP -> "signup";
            case FIND_ID -> "findId";
            case RESET_PASSWORD -> "findPassword";
        };
    }

}
