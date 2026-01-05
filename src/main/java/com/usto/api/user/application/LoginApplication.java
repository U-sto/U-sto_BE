package com.usto.api.user.application;

import com.usto.api.common.exception.LoginFailedException;
import com.usto.api.user.domain.model.LoginUser;
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
    private final PasswordEncoder passwordEncoder;

    public LoginUser login(String usrId, String rawPassword) {
        LoginUser user = userRepository.loadByUsrId(usrId)
                .orElseThrow(LoginFailedException::new);


        if (!passwordEncoder.matches(rawPassword, user.getPwHash())) {
            throw new LoginFailedException();
        }

        //세션 토큰 발급
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getUsrId(),
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        return user;
    }
}
