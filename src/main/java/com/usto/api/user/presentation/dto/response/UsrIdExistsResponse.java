package com.usto.api.user.presentation.dto.response;

public record UsrIdExistsResponse(boolean exists){
    public static UsrIdExistsResponse of (boolean exists){
        return new UsrIdExistsResponse(exists);
    }
}
