package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.common.utils.CurrentUser;
import com.usto.api.user.domain.model.LoginUser;
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

    @Transactional
    public UserUpdateResponseDto update(String usrId, UserUpdateRequestDto request) {

        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인을 하셔야합니다."); //로그인이 안된거임
        }

        if (!loginUsrId.equals(usrId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다."); //사실 불가능한 상황이긴 함
        }

        String newPwHash = null;
        if (request.getNewPw() != null) {
            newPwHash = passwordEncoder.encode(request.getNewPw()); //비밀번호 재확인 절차
        }

        User updated = userRepository.updateProfile(
                usrId,
                request.getNewUsrNm(),
                request.getNewEmail(),
                request.getNewSms(),
                newPwHash
        );

        return UserUpdateResponseDto.builder()
                .usrId(updated.getUsrId())
                .usrNm(updated.getUsrNm())
                .email(updated.getEmail())
                .sms(updated.getSms())
                .build();
    }
}

