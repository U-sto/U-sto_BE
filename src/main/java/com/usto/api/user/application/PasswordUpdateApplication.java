package com.usto.api.user.application;

import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordUpdateApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updatePwHashByUsrId(
            String userId,
            String rawPassword)
    {
        String pwHash = passwordEncoder.encode(rawPassword);
         userRepository.updatePwHashByUsrId(userId, pwHash);
    }

}
