package com.usto.api.user.application;

import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginApplication {

    private final UserRepository userRepository;

    public LoginUser login(String usrId) {

        // 1. 아이디 조회
        User user = userRepository.findByUsrId(usrId)
                .orElseThrow(LoginFailedException::invalidCredentials);

        // 2. 승인 상태 조회
        if (!user.getApprSts().isApproved()) {
            throw LoginFailedException.notApproved();
        }

        // 3. 탈퇴 회원 여부 확인
        if(user.getDelAt() != null){
            throw LoginFailedException.deleted();
        }

        return LoginUser.from(user);
    }
}
