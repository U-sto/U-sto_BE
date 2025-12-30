package com.usto.api.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder(toBuilder = true) //상속관계에서는 슈퍼
@Getter
@Setter
public class BaseTime {

    private String creBy;
    private LocalDateTime creAt;
    private String updBy;
    private LocalDateTime updAt;
}
