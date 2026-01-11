package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class LoginUser extends BaseTime {

    private final String usrId;
    private final String pwHash;
    private final String usrNm;
    private final Role roleId;

    public static LoginUser from(User user) {
        return LoginUser.builder()
                .usrId(user.getUsrId())
                .pwHash(user.getPwHash())
                .usrNm(user.getUsrNm())
                .roleId(user.getRoleId())
                .build();
    }
}
