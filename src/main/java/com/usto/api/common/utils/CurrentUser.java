package com.usto.api.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {
    private CurrentUser() {}

    public static String usrId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //토큰에서 정보를 가져온다
        if (auth == null || !auth.isAuthenticated()) return null;

        Object principal = auth.getPrincipal(); // “로그인한 주체”
        if (principal instanceof String) return (String) principal;

        // principal이 UserDetails(UserPrincipal)인 경우까지 대비하려면 여기 확장
        return null;
    }
}
