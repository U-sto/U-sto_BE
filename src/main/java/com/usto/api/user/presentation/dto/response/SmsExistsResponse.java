package com.usto.api.user.presentation.dto.response;

public record SmsExistsResponse(boolean exists){
    public static SmsExistsResponse of (boolean exists){
        return new SmsExistsResponse(exists);
    }
}
