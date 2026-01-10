package com.usto.api.user.application;

import com.usto.api.common.exception.BusinessException;
import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.common.utils.CurrentUser;
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
    //private final HttpSession session; // 로그인 userId 얻기

    @Transactional
    public UserUpdateResponseDto update(String pathUserId, UserUpdateRequestDto request) {

        String loginUsrId = CurrentUser.usrId();

        if (loginUsrId == null) {
            throw new BusinessException("로그인이 필요합니다."); //로그인이 안된거임
        }

        if (!loginUsrId.equals(pathUserId)) {
            throw new AccessDeniedException("본인만 수정 가능합니다."); //사실 불가능한 상황이긴 함
        }

        User user = userRepository
                .getByUsrId(pathUserId);

        // 들어온 것들(기존과 다른거)만 수정한다, 나머지는 그대로
        if (request.getNewUsrNm() != null && !request.getNewUsrNm().equals(user.getUsrNm())) {
            user.changeName(request.getNewUsrNm());
        }
        if (request.getNewEmail() != null && !request.getNewEmail().equals(user.getEmail())) {
            user.changeEmail(request.getNewEmail());
        }
        if (request.getNewSms() != null && !request.getNewSms().equals(user.getSms())) {
            user.changeSms(request.getNewSms());
        }
        if (request.getNewPw() != null && !request.getNewPw().equals(user.getPwHash())){
            passwordEncoder.encode(request.getNewPw());
            user.changePwHash(passwordEncoder.encode(request.getNewPw()));
        }


        // 4) 저장
        userRepository.save(user);

        return UserUpdateResponseDto.builder()
                .usrId(user.getUsrId())
                .usrNm(user.getUsrNm())
                .email(user.getEmail())
                .sms(user.getSms())
                .build();
    }
}