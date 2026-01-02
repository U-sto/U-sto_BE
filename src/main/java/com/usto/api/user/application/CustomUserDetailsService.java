package com.usto.api.user.application;

import com.usto.api.user.domain.UserPrincipal;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String usrId)
            throws UsernameNotFoundException {

        UserJpaEntity user = userJpaRepository
                .findByUsrId(usrId) //시스템에서 ID가 username이라서,,
                .orElseThrow(() ->
                        new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // UserDetails 구현체로 감싸서 반환
        return new UserPrincipal(user);
    }
}
