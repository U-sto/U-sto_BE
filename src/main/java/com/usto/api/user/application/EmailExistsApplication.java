package com.usto.api.user.application;

import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailExistsApplication {

    private final UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsrNmAndEmail(String usrNm, String email) {
        return userRepository.existsByUsrNmAndEmail(usrNm, email);
    }

    public boolean existsByUsrIdAndEmail(String usrId, String email) {
        return userRepository.existsByUsrIdAndEmail(usrId, email);
    }
}
