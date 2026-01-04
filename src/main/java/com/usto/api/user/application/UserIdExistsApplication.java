package com.usto.api.user.application;

import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserIdExistsApplication {

    private final UserRepository userRepository;

    public boolean existsByUsrId(String usrId) {
        return userRepository.existsByUsrId(usrId);
    }
}
