package com.usto.api.user.domain.model;

import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import com.usto.api.user.infrastructure.mapper.UserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//Spring Security의 UserDetails 어댑터역할
public class UserPrincipal implements UserDetails {

    private final UserJpaEntity userEntity;


    public UserPrincipal(UserJpaEntity user) {
        this.userEntity = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + userEntity.getRoleId().name());
    }

    @Override
    public String getPassword() {
            return userEntity.getPwHash();
    }

    @Override
    public String getUsername() {
            return userEntity.getUsrId();
    }

    public String getOrgCd() {
            return userEntity.getOrgCd();
    }

    @Override public boolean isAccountNonExpired() {
        return true;
    }
    @Override public boolean isAccountNonLocked() {
        return true;
    }
    @Override public boolean isCredentialsNonExpired() {
        return true;
    }

    //로그인 가능 야부 확인 - 하드코딩 된 부분을 개선
    @Override
    public boolean isEnabled() {
        User domainUser = UserMapper.toDomain(userEntity);
        return domainUser.canLogin();
    }
}
