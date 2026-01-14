package com.usto.api.user.infrastructure.repository;

import com.usto.api.common.exception.UserNotFoundException;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.infrastructure.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        UserJpaEntity entity = UserMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<String> findUsrIdByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserJpaEntity::getUsrId);
    }

    @Override
    public Optional<String> findUsrNmByUsrId(String usrId) {
        return userJpaRepository.findByUsrId(usrId)
                .map(UserJpaEntity::getUsrNm);
    }

    @Override
    @Transactional
    @Deprecated
    public void updatePwHashByUsrId(String usrId, String pwHash) {

        int updated = userJpaRepository
                .updatePwHashByUsrId(usrId, pwHash);

        if (updated == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }

    @Override
    public Optional<User> findByUsrId(String usrId) {
        return userJpaRepository.findByUsrId(usrId)
                .map(UserMapper::toDomain);
    }


    @Override
    public User getByUsrId(String pathUserId) {
        return userJpaRepository.findByUsrId(pathUserId)
                .map(UserMapper::toDomain)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public void softDeleteByUsrId(String usrId) {
        int updated = userJpaRepository.softDeleteByUsrId(usrId);
        if (updated == 0) throw new UserNotFoundException(); // 이미 삭제 포함
    }

    @Override
    public boolean existsByUsrNmAndEmail(String usrNm, String email) {
        return userJpaRepository.existsByUsrNmAndEmail(usrNm, email);
    }

    @Override
    public boolean existsByUsrIdAndEmail(String usrId, String email) {
        return userJpaRepository.existsByUsrIdAndEmail(usrId, email);
    }
}
