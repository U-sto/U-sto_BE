package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.infrastructure.entity.UserJpaEntityMapper;
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
        UserJpaEntity entity = UserJpaEntityMapper.toEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        return UserJpaEntityMapper.toDomain(saved);
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
    public void updatePwHashByUsrId(String usrId, String pwHash) {

        int updated = userJpaRepository
                .updatePwHashByUsrId(
                        usrId,
                        pwHash); //업데이트 된 행 수

        if (updated == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
    }

    @Override
    public Optional<LoginUser> loadByUsrId(String usrId) {
        return userJpaRepository.findByUsrId(usrId)
                .map(user -> LoginUser.forLogin(
                        user.getUsrId(),
                        user.getPwHash(),
                        user.getUsrNm(),
                        user.getRoleId()
                ));
    }
}

