package com.usto.api.user.infrastructure.repository;

import com.usto.api.user.domain.model.LoginUser;
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

    //User save(User user);

    Optional<UserJpaEntity> findByUsrId(String usrId);
    Optional<UserJpaEntity> findByUsrId(LoginUser usrId);

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByUsrNmAndEmail(String usrNm, String email);
    boolean existsByUsrIdAndEmail(String usrId, String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserJpaEntity u set u.pwHash = :pwHash where u.usrId = :usrId")
    int updatePwHashByUsrId(@Param("usrId") String usrId, @Param("pwHash") String pwHash);

    //User getByUsrId(LoginUser pathUserId);

    Optional<UserJpaEntity> findByUsrIdAndDelYnFalse(String usrId);

    @Modifying
    @Query("""
        update UserJpaEntity u
           set u.delYn = true,
               u.delAt = CURRENT_TIMESTAMP
         where u.usrId = :usrId
           and u.delYn = false
    """)
    int softDeleteByUsrId(@Param("usrId") String usrId);

}
