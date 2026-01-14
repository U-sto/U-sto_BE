package com.usto.api.user.domain.repository;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;

import java.util.Optional;

public interface UserRepository{

    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsBySms(String sms);

    User save(User user);

    @Deprecated
    void updatePwHashByUsrId(String usrId, String pwHash);

    Optional<String> findUsrIdByEmail(String email);
    Optional<String> findUsrNmByUsrId(String usrId);
    Optional<User> findByUsrId(String usrId);
    Optional<User> findAdminByOrgCd(String orgCd);
    User getByUsrId(String pathUserId);

    void softDeleteByUsrId(String usrId);

    boolean existsByUsrNmAndEmail(String usrNm, String email);
    boolean existsByUsrIdAndEmail(String usrId, String email);
}
