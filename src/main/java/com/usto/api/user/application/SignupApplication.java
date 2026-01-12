package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(
            SignupRequestDto request,
            String verifiedEmail,
            String verifiedSms) {
        String pwHash = passwordEncoder.encode(request.getPwd());

        //회원 탈퇴한 회원과 같은 이메일과 전화번호를 쓰려고 하면 막아야한다? 조금 이상한 정책으로 보임,, 어떻게 풀어나가야할지 고민
        //만약에 막아야한다면 여기서 막으면 됌

        User user = User.builder()
                .usrId(request.getUsrId())
                .usrNm(request.getUsrNm())
                .pwHash(pwHash)
                .email(verifiedEmail)
                .sms(verifiedSms)
                .orgCd(request.getOrgCd())
                .apprSts(ApprovalStatus.WAIT)
                .roleId(Role.GUEST)
                .build();

        userRepository.save(user);
    }
}

