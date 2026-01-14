package com.usto.api.user.application;

import com.usto.api.user.domain.model.User;
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
    public void updatePassword(String usrId, String newPassword)
    {
        //사용자 조회
        User user = userRepository.getByUsrId(usrId);
        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        //Domain메서드로 변경
        User updated = user.changePassword(encodedPassword);
        //저장
        userRepository.save(updated);
    }
}
