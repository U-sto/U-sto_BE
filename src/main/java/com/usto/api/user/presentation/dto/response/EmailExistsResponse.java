package com.usto.api.user.presentation.dto.response;

public record EmailExistsResponse(boolean exists){
    public static EmailExistsResponse of (boolean exists){
        return new EmailExistsResponse(exists);
    }
}
