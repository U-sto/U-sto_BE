package com.usto.api.user.domain;

import com.usto.api.user.domain.model.ApprovalStatus;
import com.usto.api.user.domain.model.LoginUser;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.infrastructure.entity.UserJpaEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final UserJpaEntity user;
    private final LoginUser loginUser;


    public UserPrincipal(UserJpaEntity user) {
        this.user = user;
        this.loginUser = null;
    }

    public UserPrincipal(LoginUser loginUser) {
        this.user = null;
        this.loginUser = loginUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user != null) {
            return List.of(()
                    -> "ROLE_" + user.getRoleId().name());
        }
            return List.of(()
                    -> "ROLE_" + loginUser.getRoleId().name());
    }

    @Override
    public String getPassword() {
        if (user != null)
            return user.getPwHash();
            return loginUser.getPwHash();
    }

    @Override
    public String getUsername() {
        if (user != null)
            return user.getUsrId();
            return loginUser.getUsrId();
    }

    public String getOrgCd() {
        if (user != null)
            return user.getOrgCd();
            return null;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    //로그인 가능 조건
    @Override
    public boolean isEnabled() {
        return user.getRoleId() != Role.GUEST
                && user.getApprSts() == ApprovalStatus.APPROVED;
    }
}
