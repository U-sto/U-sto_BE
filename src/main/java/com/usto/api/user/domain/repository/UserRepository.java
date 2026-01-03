package com.usto.api.user.domain.repository;


import com.usto.api.user.domain.model.User;

import java.util.Optional;

public interface UserRepository{

    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsBySms(String sms);

    User save(User user);

    Optional<String> findUsrIdByEmail(String email);
    Optional<String> findUsrNmByUsrId(String usrId);
    void updatePasswordHash(String usrId, String pwHash);
}
