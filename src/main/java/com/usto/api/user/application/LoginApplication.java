package com.usto.api.user.application;

import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginApplication {

    private final UserRepository userRepository;

    public User login(String usrId) {

        // 1. 아이디 조회
        User user = userRepository.findByUsrId(usrId)
                .orElseThrow(LoginFailedException::invalidCredentials);

        // 2. 승인 상태 조회
        if (!user.getApprSts().isApproved()) {
            throw LoginFailedException.notApproved();
        }

        // 3. 탈퇴 회원 여부 확인
        if("Y".equals(user.isDelYn())){ //Boolean 타입은 is ~ 로
            throw LoginFailedException.deleted();
        }

        return user;
    }
}
