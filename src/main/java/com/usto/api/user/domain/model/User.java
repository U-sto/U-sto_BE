package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class User extends BaseTime {

    private String usrId;
    private String usrNm;
    private String pwHash;
    private String email;
    private String sms;
    private Role roleId;
    private String orgCd;
    private ApprovalStatus apprSts;

}