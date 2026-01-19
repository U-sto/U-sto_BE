package com.usto.api.user.domain.model;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("조직 관리자"),
    MANAGER("물품 운용관"),
    GUEST("승인되지 않은 사용자");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
