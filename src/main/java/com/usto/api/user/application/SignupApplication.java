package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.presentation.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignupApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendApplication emailSendApplication;

    @Transactional
    public void signup(
            String UsrId,
            String UsrNm,
            String pwd,
            String orgCd,
            String verifiedEmail,
            String verifiedSms) {
        String pwHash = passwordEncoder.encode(pwd);

        if (verifiedEmail == null) {
            throw new BusinessException("이메일 인증이 필요합니다.");
        }

        if (verifiedSms == null) {
            throw new BusinessException("휴대폰 인증이 필요합니다.");
        }

        //회원 탈퇴한 회원과 같은 이메일과 전화번호를 쓰려고 하면 막아야한다? 조금 이상한 정책으로 보임,, 어떻게 풀어나가야할지 고민
        //만약에 막아야한다면 여기서 막으면 됌

        User user = User.builder()
                .usrId(UsrId)
                .usrNm(UsrNm)
                .pwHash(pwHash)
                .email(verifiedEmail)
                .sms(verifiedSms)
                .orgCd(orgCd)
                .apprSts(ApprovalStatus.WAIT)
                .roleId(Role.GUEST)
                .build();

        userRepository.save(user);

        emailSendApplication.sendApprovalRequestEmail(user);
    }
}

