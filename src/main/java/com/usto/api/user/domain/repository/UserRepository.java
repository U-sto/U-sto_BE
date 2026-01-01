package com.usto.api.user.domain.repository;

public interface UserRepository {

    boolean existsByUsrId(String usrId);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    /* 아직은 구현 X
    Optional<User> findByUsrId(String usrId); // 로그인/조회

    User save(User user); // 회원가입 저장용 (선택)
     */
}
