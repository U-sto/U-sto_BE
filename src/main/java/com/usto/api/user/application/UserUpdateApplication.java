package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.CurrentUser;
import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.response.UserInfoResponseDto;
import com.usto.api.user.presentation.dto.response.UserPwdUpdateResponseDto;
import com.usto.api.user.presentation.dto.response.UserSmsUpdateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdateApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationJpaRepository organizationJpaRepository;

    @Transactional
    public UserInfoResponseDto info (String usrId) {

        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인이 필요합니다");
        }
        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 조회 가능합니다.");
        }

        User user = userRepository.getByUsrId(usrId);

        //다른 애들도 가져오기(수정은 안되지만 보여주긴 해야하는 것들)
        String roleNm = user.getRoleId().displayName();
        String orgNm = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm)
                .orElse(null);

        return UserInfoResponseDto.builder()
                .usrId(user.getUsrId())
                .usrNm(user.getUsrNm())
                .email(user.getEmail())
                .sms(user.getSms())
                .orgNm(orgNm)
                .roleNm(roleNm)
                .build();
    }

    @Transactional
    public UserPwdUpdateResponseDto updatePwd(String usrId, String newPwd) {
        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인이 필요합니다");
        }
        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다.");
        }

        //사용자 조회
        User user = userRepository.getByUsrId(usrId);
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPwd);
        //Domain메서드로 변경
        User updated = user.changePassword(encodedPassword);
        //저장
        User saved = userRepository.save(updated);

        return UserPwdUpdateResponseDto.builder()
                .usrId(saved.getUsrId())
                .newPwd(saved.getPwHash())
                .build();
    }

    @Transactional
    public UserSmsUpdateResponseDto updateSms(String usrId, String target) {
        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인이 필요합니다");
        }
        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다.");
        }

        User user = userRepository.getByUsrId(usrId);

        if(userRepository.existsBySms(target)){
            throw new BusinessException("기존과 동일한 전화번호 입니다.");
        }

        User updated = user.changeSms(target);

        User saved = userRepository.save(updated);

        return UserSmsUpdateResponseDto.builder()
                .usrId(saved.getUsrId())
                .newSms(saved.getSms())
                .build();    }
}

