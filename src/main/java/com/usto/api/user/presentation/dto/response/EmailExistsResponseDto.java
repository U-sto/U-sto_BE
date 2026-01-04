package com.usto.api.user.presentation.dto.response;

public record EmailExistsResponseDto (boolean exists){
    public static EmailExistsResponseDto of (boolean exists){
        return new EmailExistsResponseDto(exists);
    }
}
