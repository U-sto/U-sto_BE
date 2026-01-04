package com.usto.api.user.presentation.dto.response;

public record SmsExistsResponseDto(boolean exists){
    public static SmsExistsResponseDto of (boolean exists){
        return new SmsExistsResponseDto(exists);
    }
}
