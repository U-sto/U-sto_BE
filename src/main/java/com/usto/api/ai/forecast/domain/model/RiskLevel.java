package com.usto.api.ai.forecast.domain.model;

public enum RiskLevel {
    HIGH,
    MEDIUM,
    LOW;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
