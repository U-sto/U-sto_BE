package com.usto.api.user.application;

import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsrIdExistsApplication {

    private final UserRepository userRepository;

    public boolean existsByUsrId(String usrId) {
        return userRepository.existsByUsrId(usrId);
    }
}
