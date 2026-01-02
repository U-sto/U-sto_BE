package com.usto.api.user.domain.repository;


import com.usto.api.user.domain.model.User;

public interface UserRepository{

    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsBySms(String sms);

    User save(User user);
}
