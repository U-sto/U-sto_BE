package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.User;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, String> {

    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsBySms(String sms);

    User save(User user);

    Optional<Object> findByUsrId(String usrId);

    Optional<UserJpaEntity> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserJpaEntity u set u.pwHash = :pwHash where u.usrId = :usrId")
    int updatePwHashByUsrId(@Param("usrId") String usrId, @Param("pwHash") String pwHash);
}
