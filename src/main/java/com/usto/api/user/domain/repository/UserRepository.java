package com.usto.api.user.domain.repository;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.model.User;

import java.util.Optional;

public interface UserRepository{

    //존재 여부 확인
    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsBySms(String sms);

    User save(User user);

    //이메일 인증을 위한..
    Optional<String> findUsrIdByEmail(String email);
    Optional<String> findUsrNmByUsrId(String usrId);
    void updatePwHashByUsrId(String usrId, String pwHash);

    //로그인을 위한
    Optional<User> findByUsrId(String usrId);

    User getByUsrId(String pathUserId);
    User updateProfile(String usrId, String usrNm, String email, String sms, String pwHash);
}
