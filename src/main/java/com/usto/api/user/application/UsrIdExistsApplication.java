package com.usto.api.user.application;

import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsrIdExistsApplication {

    private final UserJpaRepository userJpaRepository;

    public boolean existsByUsrId(String usrId) {
        return userJpaRepository.existsByUsrId(usrId);
    }
}
