package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    // 회원조회
    Optional<UserJpaEntity> findByUsrId(String usrId);


    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
