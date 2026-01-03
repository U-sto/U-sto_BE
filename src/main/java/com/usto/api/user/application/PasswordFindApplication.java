package com.usto.api.user.application;

import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordFindApplication {

    private final UserRepository userRepository;

    public String findUserIdByEmail(String email) {
        return userRepository
                .findUsrIdByEmail(email)
                .orElse(null);
    }
}
