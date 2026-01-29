package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignupApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSendApplication emailSendApplication;
    private final OrganizationJpaRepository organizationJpaRepository;

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

        String orgName = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm) // 여기서 이름을 꺼냄
                .orElse("알 수 없는 조직");

        emailSendApplication.sendApprovalRequestEmail(user,orgName);
    }
}

