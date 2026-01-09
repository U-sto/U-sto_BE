package com.usto.api.user.domain.model;

import com.usto.api.common.BaseTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.print.attribute.standard.PrinterURI;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
public class LoginUser extends BaseTime {

    private final String usrId;
    private final String pwHash;
    private final String usrNm;
    private final Role roleId;

    public static LoginUser forLogin(
            String usrId,
            String pwHash,
            String usrNm,
            Role roleId
    ) {
        return LoginUser.builder()
                .usrId(usrId)
                .pwHash(pwHash)
                .usrNm(usrNm)
                .roleId(roleId)
                .build();
    }
}
