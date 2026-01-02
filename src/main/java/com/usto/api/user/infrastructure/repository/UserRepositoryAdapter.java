package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.infrastructure.entity.UserJpaEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByUsrId(String usrId) {
        return userJpaRepository.existsByUsrId(usrId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsBySms(String sms) {
        return userJpaRepository.existsBySms(sms);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntityMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserJpaEntityMapper.toDomain(saved);
    }
}

