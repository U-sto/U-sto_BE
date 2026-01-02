package com.usto.api.user.application;

import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailExistsApplication {

    private final UserJpaRepository userJpaRepository;

    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
