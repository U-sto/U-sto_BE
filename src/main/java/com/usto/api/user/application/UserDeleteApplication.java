package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.UserPrincipal;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.presentation.dto.request.UserDeleteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDeleteApplication {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteMe(UserPrincipal me, UserDeleteRequestDto request) {

        if (request == null || request.getCurrentPw() == null || request.getCurrentPw().isBlank()) {
            throw new BusinessException("현재 비밀번호가 필요합니다.");
        }

        // pwHash만 필요하면: 도메인 로드 or pwHash 전용 조회 메서드
        var user = userRepository.getByUsrId(me.getUsername()); // delYn=false 기준이어야 함

        if (!passwordEncoder.matches(request.getCurrentPw(), user.getPwHash())) {
            throw new BusinessException("현재 비밀번호가 올바르지 않습니다.");
        }

        userRepository.softDeleteByUsrId(me.getUsername());
    }
}
