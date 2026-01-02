package com.usto.api.user.application;

import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsExistsApplication {

    private final UserJpaRepository userJpaRepository;

    public boolean existsBySms(String sms) {
        return userJpaRepository.existsBySms(sms);
    }
}
