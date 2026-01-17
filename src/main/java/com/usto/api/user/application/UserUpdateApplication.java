package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.utils.CurrentUser;
import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.request.UserUpdateRequestDto;
import com.usto.api.user.presentation.dto.response.UserUpdateResponseDto;
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
            throw new BusinessException("로그인이 필요합니다");
        }
        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다.");
        }

        User user = userRepository.getByUsrId(usrId);

        User updated = user.updateProfile(
                request.getNewUsrNm(),
                request.getNewSms()
        );

        //비밀번호 변경이 있다면 (로직 분리)
        if (request.getNewPw() != null && ! request.getNewPw().isBlank()) {
            String encodedPw = passwordEncoder.encode(request.getNewPw());
            updated = updated.changePassword(encodedPw);
        }

        User saved = userRepository.save(updated);

        //다른 애들도 가져오기(수정은 안되지만 보여주긴 해야하는 것들)
        String roleNm = user.getRoleId().displayName();
        String orgNm = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm)
                .orElse(null);

        return UserUpdateResponseDto.builder()
                .usrId(saved.getUsrId())
                .usrNm(saved.getUsrNm())
                .email(saved.getEmail())
                .sms(saved.getSms()) //있다면
                .orgNm(orgNm)
                .roleNm(roleNm)
                .build();
    }
}

