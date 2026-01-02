package com.usto.api.user.presentation.dto.response;

public record UsrIdExistsResponseDto(boolean exists){
    public static UsrIdExistsResponseDto of (boolean exists){
        return new UsrIdExistsResponseDto(exists);
    }
}
