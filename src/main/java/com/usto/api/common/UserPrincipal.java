package com.usto.api.common;

import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final UserJpaEntity user;


    public UserPrincipal(UserJpaEntity user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + user.getRoleId().name());
    }

    @Override
    public String getPassword() {
            return user.getPwHash();
    }

    @Override
    public String getUsername() {
            return user.getUsrId();
    }

    public String getOrgCd() {
            return user.getOrgCd();
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

    //로그인 가능 조건
    @Override
    public boolean isEnabled() {
        return user.getRoleId() != Role.GUEST
                && user.getApprSts() == ApprovalStatus.APPROVED;
    }
}
