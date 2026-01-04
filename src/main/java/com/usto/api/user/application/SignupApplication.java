package com.usto.api.user.application;

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

