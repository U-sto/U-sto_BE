package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.common.utils.CurrentUser;
import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.request.UserUpdateRequestDto;
import com.usto.api.user.presentation.dto.response.UserUpdateResponseDto;
import jakarta.servlet.http.HttpSession;
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
    public UserUpdateResponseDto update(String usrId, UserUpdateRequestDto request) {

        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인이 필요합니다"); //세션 만료
        }

        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다."); //사실 불가능한 상황이긴 함
        }

        String newPwHash = null;
        if (request.getNewPw() != null) {
            newPwHash = passwordEncoder.encode(request.getNewPw()); //비밀번호 재확인 절차
        }



        User updated = userRepository.updateProfile(
                usrId, //고정으로 따라가는 놈(식별자 역할)
                request.getNewUsrNm(),
                request.getNewEmail(),
                request.getNewSms(),
                newPwHash
        );

        //역할이름 가져오기
        User user = userRepository.getByUsrId(usrId);
        String roleNm = user.getRoleId().displayName();

        //조직 이름 가져오기
        String orgNm = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm)
                .orElse(null);

        return UserUpdateResponseDto.builder()
                .usrId(updated.getUsrId())
                .usrNm(updated.getUsrNm())
                .email(updated.getEmail())
                .sms(updated.getSms())
                .orgNm(orgNm)
                .roleNm(roleNm)
                .build();
    }
}

