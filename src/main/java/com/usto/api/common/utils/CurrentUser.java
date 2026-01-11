package com.usto.api.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class CurrentUser {
    private CurrentUser() {}

    public static String usrId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 인증 객체가 없거나, 익명 사용자("anonymousUser")인 경우 null 반환
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) return null;

        Object principal = auth.getPrincipal();

        // 1. Principal이 UserDetails 타입인 경우 (DaoAuthenticationProvider 사용 시 일반적인 경우)
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        // 2. Principal이 String 타입인 경우
        if (principal instanceof String) {
            return (String) principal;
        }

        return null;
    }
}
